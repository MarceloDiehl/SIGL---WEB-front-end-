-- Dados de demonstração: carregados só com o perfil "demo" (application-demo.properties).
-- Script repetível do Flyway (R__): roda depois das migrações e de novo se for alterado.
-- Cada INSERT só grava o que ainda não existe, então pode rodar num banco que já tem dados.
--
-- Contas para entrar (senhas em BCrypt; documentadas no README):
--   gerente@sigl.demo    / gerente123   (Gerente)
--   vendedor@sigl.demo   / vendedor123  (Vendedor)
--   estoque@sigl.demo    / estoque123   (Estoquista)

-- ---------- Funcionários: um de cada cargo ----------
INSERT INTO `cadastrofuncionario` (`nome`, `sobrenome`, `dataNascimento`, `idCargo`, `idEscolaridade`, `especialidade`,
    `telefone`, `CPF`, `idGenero`, `endereco`, `numeroPredial`, `bairro`, `cidade`, `estado`, `CEP`,
    `contatoEmergencia`, `dataEntrada`, `RG`, `CTPAS`, `email`, `senha`)
SELECT 'Diego', 'Almeida', '1988-01-30', 2, 8, 'Equipamentos de áudio e raridades',
    '(51) 98222-6677', '390.533.447-05', 2, 'Rua Fernandes Vieira', 230, 'Bom Fim', 'Porto Alegre', 'RS', '90035-090',
    '(51) 99111-2233', '2020-09-15', '4012345678', '7654321/002-0', 'gerente@sigl.demo',
    '{bcrypt}$2a$10$PsD/Y2KojJYFQd70l6D88ejFttlU8I8nbL8Cy5NkZQAHfcL.cMvh6'
WHERE NOT EXISTS (SELECT 1 FROM `cadastrofuncionario` WHERE `email` = 'gerente@sigl.demo');

INSERT INTO `cadastrofuncionario` (`nome`, `sobrenome`, `dataNascimento`, `idCargo`, `idEscolaridade`, `especialidade`,
    `telefone`, `CPF`, `idGenero`, `endereco`, `numeroPredial`, `bairro`, `cidade`, `estado`, `CEP`,
    `contatoEmergencia`, `dataEntrada`, `RG`, `CTPAS`, `email`, `senha`)
SELECT 'Marina', 'Costa', '1996-06-21', 1, 7, 'MPB e jazz brasileiro',
    '(51) 99120-3344', '153.509.460-56', 1, 'Rua Lima e Silva', 88, 'Cidade Baixa', 'Porto Alegre', 'RS', '90050-100',
    '(51) 99777-1122', '2022-03-01', '3012345678', '1234567/001-0', 'vendedor@sigl.demo',
    '{bcrypt}$2a$10$ryU9gfk1SIMy.239wDo43uNHHsoAtmrfhfi.k6znBet9q3p2cjrtC'
WHERE NOT EXISTS (SELECT 1 FROM `cadastrofuncionario` WHERE `email` = 'vendedor@sigl.demo');

INSERT INTO `cadastrofuncionario` (`nome`, `sobrenome`, `dataNascimento`, `idCargo`, `idEscolaridade`, `especialidade`,
    `telefone`, `CPF`, `idGenero`, `endereco`, `numeroPredial`, `bairro`, `cidade`, `estado`, `CEP`,
    `contatoEmergencia`, `dataEntrada`, `RG`, `CTPAS`, `email`, `senha`)
SELECT 'Bruno', 'Lopes', '1999-12-03', 3, 4, 'Organização de acervo e importados',
    '(51) 99654-7788', '482.193.705-05', 2, 'Rua Sarmento Leite', 51, 'Centro Histórico', 'Porto Alegre', 'RS', '90050-170',
    '(51) 99321-4455', '2024-05-06', '5012345678', '1122334/003-0', 'estoque@sigl.demo',
    '{bcrypt}$2a$10$GrSPA81uuhQQIb5vywOvXOSeoxGbiDWglkp4ozpRiTc/iSEk70CnS'
WHERE NOT EXISTS (SELECT 1 FROM `cadastrofuncionario` WHERE `email` = 'estoque@sigl.demo');

-- ---------- Produtos: pelo menos um de cada tipo (codProduto é único) ----------
INSERT IGNORE INTO `cadastroproduto` (`codProduto`, `titulo`, `descricao`, `valor`, `quantidade`, `entrada`, `idTipo`, `idCondicao`) VALUES
('100231', 'Pink Floyd – The Dark Side of the Moon (LP 180g)', 'Reedição 2016, capa gatefold com pôsteres e adesivos originais. Lacrado.', 289.90, 6, '2025-03-12', 1, 1),
('100418', 'Clube da Esquina – Milton Nascimento & Lô Borges', 'Prensagem nacional 1980, LP duplo. Capa com leve desgaste nas bordas; discos em ótimo estado.', 420.00, 1, '2025-04-02', 1, 2),
('200077', 'Tim Maia – Racional Vol. 1 (CD)', 'CD remasterizado com encarte de 12 páginas.', 59.90, 14, '2025-05-20', 2, 1),
('300045', 'Caderno Orion – Pautado A5', 'Capa dura com o disco da Orion, 160 folhas.', 39.90, 25, '2025-05-28', 3, 1),
('600201', 'Livro – Discobiografia Legião Urbana', 'Capa dura, 320 páginas, edição comemorativa.', 119.00, 5, '2025-08-14', 4, 1),
('400150', 'Camiseta Orion – Vinil Clássico (M)', 'Algodão 100%, estampa em serigrafia, preta.', 79.90, 0, '2025-06-18', 5, 1),
('300912', 'Toca-discos Audio-Technica AT-LP60X', 'Toca-discos automático, correia, pré-amplificador embutido. Bivolt.', 1499.00, 2, '2025-06-01', 6, 1),
('700310', 'Violão Giannini GF-1D Folk', 'Tampo em spruce, cordas de aço. Revisado e regulado.', 899.00, 3, '2025-07-01', 7, 3),
('500033', 'Os Mutantes – Os Mutantes (1968) 1ª prensagem', 'Raridade. Selo Polydor, mono. Marcas de uso leves, toca sem pulos.', 2350.00, 1, '2025-07-09', 8, 4);

-- ---------- Clientes ----------
INSERT INTO `cadastrocliente` (`nome`, `sobrenome`, `dataNascimento`, `CPF`, `RG`, `endereco`, `numeroPredial`,
    `bairro`, `cidade`, `estado`, `CEP`, `telefone`, `clienteDesde`)
SELECT 'Juliana', 'Prates', '1991-04-17', '529.982.247-25', '1098765432', 'Rua da República', 412,
    'Cidade Baixa', 'Porto Alegre', 'RS', '90050-320', '(51) 99812-4410', '2023-02-10'
WHERE NOT EXISTS (SELECT 1 FROM `cadastrocliente` WHERE `CPF` = '529.982.247-25');

INSERT INTO `cadastrocliente` (`nome`, `sobrenome`, `dataNascimento`, `CPF`, `RG`, `endereco`, `numeroPredial`,
    `bairro`, `cidade`, `estado`, `CEP`, `telefone`, `clienteDesde`)
SELECT 'Rafael', 'Kunz', '1985-11-02', '111.444.777-35', '2087654321', 'Av. Osvaldo Aranha', 1200,
    'Bom Fim', 'Porto Alegre', 'RS', '90035-191', '(51) 98455-0091', '2024-08-01'
WHERE NOT EXISTS (SELECT 1 FROM `cadastrocliente` WHERE `CPF` = '111.444.777-35');

INSERT INTO `cadastrocliente` (`nome`, `sobrenome`, `dataNascimento`, `CPF`, `RG`, `endereco`, `numeroPredial`,
    `bairro`, `cidade`, `estado`, `CEP`, `telefone`, `clienteDesde`)
SELECT 'Camila', 'Rocha', '1978-09-25', '731.052.846-80', '3076543210', 'Rua Padre Chagas', 300,
    'Moinhos de Vento', 'Porto Alegre', 'RS', '90570-080', '(51) 99300-1288', '2022-11-19'
WHERE NOT EXISTS (SELECT 1 FROM `cadastrocliente` WHERE `CPF` = '731.052.846-80');
