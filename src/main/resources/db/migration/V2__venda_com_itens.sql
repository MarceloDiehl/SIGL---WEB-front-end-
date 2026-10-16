-- Venda passa a ter cabeçalho (venda) e itens (itemvenda).
-- Antes, cada produto vendido era uma linha solta em cadastrovendas, sem número de venda
-- nem total da compra, e sem chaves estrangeiras.

CREATE TABLE `venda` (
  `id` int NOT NULL AUTO_INCREMENT,
  `dataHora` datetime NOT NULL,
  `idVendedor` int NOT NULL,
  `idMetodoPagamento` int NOT NULL,
  `cpfNota` varchar(11) DEFAULT NULL,
  `valorTotal` decimal(10,2) NOT NULL,
  `valorRecebido` decimal(10,2) DEFAULT NULL,
  `troco` decimal(10,2) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'CONCLUIDA',
  `canceladaEm` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_venda_dataHora` (`dataHora`),
  CONSTRAINT `fk_venda_vendedor` FOREIGN KEY (`idVendedor`) REFERENCES `cadastrofuncionario` (`id`),
  CONSTRAINT `fk_venda_metodopagamento` FOREIGN KEY (`idMetodoPagamento`) REFERENCES `metodopagamento` (`id`),
  CONSTRAINT `ck_venda_status` CHECK (`status` IN ('CONCLUIDA', 'CANCELADA')),
  CONSTRAINT `ck_venda_valorTotal` CHECK (`valorTotal` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- codProduto, tituloProduto e valorUnitario são a "fotografia" do produto no momento da venda:
-- se o preço ou o título mudarem depois, a venda registrada não muda.
CREATE TABLE `itemvenda` (
  `id` int NOT NULL AUTO_INCREMENT,
  `idVenda` int NOT NULL,
  `idProduto` int NOT NULL,
  `codProduto` varchar(6) NOT NULL,
  `tituloProduto` varchar(70) NOT NULL,
  `valorUnitario` decimal(9,2) NOT NULL,
  `quantidade` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_itemvenda_codProduto` (`codProduto`),
  CONSTRAINT `fk_itemvenda_venda` FOREIGN KEY (`idVenda`) REFERENCES `venda` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_itemvenda_produto` FOREIGN KEY (`idProduto`) REFERENCES `cadastroproduto` (`id`),
  CONSTRAINT `ck_itemvenda_quantidade` CHECK (`quantidade` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Migração: não há como saber quais linhas antigas eram da mesma compra,
-- então cada linha de cadastrovendas vira uma venda com um item (mesmo id).
INSERT INTO `venda` (`id`, `dataHora`, `idVendedor`, `idMetodoPagamento`, `cpfNota`, `valorTotal`, `status`)
SELECT v.`id`, TIMESTAMP(v.`dataVenda`), v.`idVendedor`, v.`idMetodoPagamento`,
       NULLIF(REGEXP_REPLACE(v.`cpfNota`, '[^0-9]', ''), ''), v.`valorTotal`, 'CONCLUIDA'
FROM `cadastrovendas` v;

INSERT INTO `itemvenda` (`idVenda`, `idProduto`, `codProduto`, `tituloProduto`, `valorUnitario`, `quantidade`, `subtotal`)
SELECT v.`id`, p.`id`, v.`codProduto`, v.`tituloProduto`, v.`valorUnitario`, v.`quantidade`, v.`valorTotal`
FROM `cadastrovendas` v
JOIN `cadastroproduto` p ON p.`codProduto` = v.`codProduto`;
