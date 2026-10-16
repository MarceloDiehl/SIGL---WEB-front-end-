-- Schema do SIGL como estava antes do versionamento com Flyway (banco loja_orion_UC15).
-- Num banco existente o Flyway só registra esta versão (baseline); num banco vazio, cria tudo.

-- ---------- Tabelas de apoio ----------

CREATE TABLE `cargo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nomeCargo` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `cargo` VALUES (1,'Vendedor');
INSERT INTO `cargo` VALUES (2,'Gerente');
INSERT INTO `cargo` VALUES (3,'Estoquista');

CREATE TABLE `condicao` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descricao` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `condicao` VALUES (1,'Produto Novo');
INSERT INTO `condicao` VALUES (2,'Usado (Ótimo)');
INSERT INTO `condicao` VALUES (3,'Usado (Bom)');
INSERT INTO `condicao` VALUES (4,'Usado (Marcas de Uso');

CREATE TABLE `escolaridade` (
  `id` int NOT NULL AUTO_INCREMENT,
  `formacao` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `escolaridade` VALUES (1,'Ensino Fundamental Incompleto');
INSERT INTO `escolaridade` VALUES (2,'Ensino Fundamental Completo');
INSERT INTO `escolaridade` VALUES (3,'Ensino Médio incompleto');
INSERT INTO `escolaridade` VALUES (4,'Ensino Médio Completo');
INSERT INTO `escolaridade` VALUES (5,'Ensino Técnico Profissionalizante');
INSERT INTO `escolaridade` VALUES (6,'Ensino Superio Incompleto');
INSERT INTO `escolaridade` VALUES (7,'Ensino Superior Completo');
INSERT INTO `escolaridade` VALUES (8,'Pós-graduação');
INSERT INTO `escolaridade` VALUES (9,'Mestrado');
INSERT INTO `escolaridade` VALUES (10,'Doutorado');

CREATE TABLE `genero` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descricao` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `genero` VALUES (1,'Feminino');
INSERT INTO `genero` VALUES (2,'Masculino');
INSERT INTO `genero` VALUES (3,'Não informado');
INSERT INTO `genero` VALUES (4,'Outro');
INSERT INTO `genero` VALUES (5,'Prefiro não declarar');

CREATE TABLE `metodopagamento` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descricao` varchar(55) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `metodopagamento` VALUES (1,'Dinheiro');
INSERT INTO `metodopagamento` VALUES (2,'Crédito');
INSERT INTO `metodopagamento` VALUES (3,'Débito');
INSERT INTO `metodopagamento` VALUES (4,'PIX');

CREATE TABLE `tipo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descricao` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `tipo` VALUES (1,'Disco de Vinil');
INSERT INTO `tipo` VALUES (2,'Disco de Vinil');
INSERT INTO `tipo` VALUES (3,'CD - Compact Disc');
INSERT INTO `tipo` VALUES (4,'Artigos de Papelaria');
INSERT INTO `tipo` VALUES (5,'Livros');
INSERT INTO `tipo` VALUES (6,'Moda e Vestuário');
INSERT INTO `tipo` VALUES (7,'Equipamentos Eletrônicos');
INSERT INTO `tipo` VALUES (8,'Instrumentos Musicais');
INSERT INTO `tipo` VALUES (9,'Raridades');

-- ---------- Cadastros e vendas ----------

CREATE TABLE `cadastrocliente` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) NOT NULL,
  `sobrenome` varchar(45) NOT NULL,
  `dataNascimento` date NOT NULL,
  `CPF` varchar(14) NOT NULL,
  `RG` varchar(14) NOT NULL,
  `endereco` varchar(45) NOT NULL,
  `numeroPredial` int NOT NULL,
  `bairro` varchar(45) NOT NULL,
  `cidade` varchar(45) NOT NULL,
  `estado` char(45) NOT NULL,
  `CEP` varchar(9) DEFAULT NULL,
  `telefone` varchar(15) DEFAULT NULL,
  `clienteDesde` date NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cadastrofuncionario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(45) NOT NULL,
  `sobrenome` varchar(45) NOT NULL,
  `dataNascimento` date NOT NULL,
  `idCargo` int NOT NULL,
  `idEscolaridade` int NOT NULL,
  `especialidade` varchar(45) DEFAULT NULL,
  `telefone` varchar(14) DEFAULT NULL,
  `CPF` varchar(14) NOT NULL,
  `idGenero` int NOT NULL,
  `endereco` varchar(45) NOT NULL,
  `numeroPredial` int NOT NULL,
  `bairro` varchar(45) NOT NULL,
  `cidade` varchar(45) NOT NULL,
  `estado` char(2) NOT NULL,
  `CEP` varchar(9) DEFAULT NULL,
  `contatoEmergencia` varchar(14) NOT NULL,
  `dataEntrada` date NOT NULL,
  `RG` varchar(14) NOT NULL,
  `CTPAS` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `senha` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idCargo` (`idCargo`),
  KEY `idEscolaridade` (`idEscolaridade`),
  KEY `idGenero` (`idGenero`),
  CONSTRAINT `cadastrofuncionario_ibfk_1` FOREIGN KEY (`idCargo`) REFERENCES `cargo` (`id`),
  CONSTRAINT `cadastrofuncionario_ibfk_2` FOREIGN KEY (`idEscolaridade`) REFERENCES `escolaridade` (`id`),
  CONSTRAINT `cadastrofuncionario_ibfk_3` FOREIGN KEY (`idGenero`) REFERENCES `genero` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cadastroproduto` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codProduto` varchar(6) NOT NULL,
  `titulo` varchar(70) NOT NULL,
  `valor` decimal(9,2) NOT NULL,
  `quantidade` int NOT NULL,
  `entrada` date NOT NULL,
  `idTipo` int NOT NULL,
  `idCondicao` int NOT NULL,
  `descricao` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codProduto` (`codProduto`),
  KEY `idTipo` (`idTipo`),
  KEY `idCondicao` (`idCondicao`),
  CONSTRAINT `cadastroproduto_ibfk_1` FOREIGN KEY (`idTipo`) REFERENCES `tipo` (`id`),
  CONSTRAINT `cadastroproduto_ibfk_2` FOREIGN KEY (`idCondicao`) REFERENCES `condicao` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `cadastrovendas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codProduto` varchar(6) NOT NULL,
  `tituloProduto` varchar(70) NOT NULL,
  `valorUnitario` decimal(9,2) NOT NULL,
  `quantidade` int NOT NULL,
  `valorTotal` decimal(9,2) NOT NULL,
  `dataVenda` date NOT NULL,
  `idVendedor` int NOT NULL,
  `cpfNota` varchar(14) DEFAULT NULL,
  `idMetodoPagamento` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
