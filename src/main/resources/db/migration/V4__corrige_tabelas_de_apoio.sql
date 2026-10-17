-- Tabelas de apoio passam a ser a fonte única das opções das telas (antes estavam fixas no HTML/JS).
-- Antes disso, os textos e a numeração precisam estar certos.

-- ---------- tipo ----------
-- "Disco de Vinil" aparecia nos ids 1 e 2, o que deslocava os demais tipos em relação às telas
-- (ex.: produto com idTipo 2 aparecia como "CD" nas telas e "Disco de Vinil" no banco).
-- A numeração correta é a que as telas sempre usaram: 1 a 8.

-- Produtos de demonstração cadastrados com idTipo 2 ou 3 são instrumentos e equipamentos:
-- reclassificados antes de renomear os tipos, para nenhum produto mudar de categoria por engano.
UPDATE `cadastroproduto` SET `idTipo` = 7   -- Instrumentos Musicais
WHERE `idTipo` IN (2, 3)
  AND `codProduto` IN ('132455', '132444', '132447', '132438', '132452', '132453', '132440', '132441');

UPDATE `cadastroproduto` SET `idTipo` = 6   -- Equipamentos Eletrônicos
WHERE `idTipo` = 2
  AND `codProduto` IN ('132442', '132449', '132450', '132445');

UPDATE `tipo` SET `descricao` = 'CD'                       WHERE `id` = 2;
UPDATE `tipo` SET `descricao` = 'Artigos de Papelaria'     WHERE `id` = 3;
UPDATE `tipo` SET `descricao` = 'Livros'                   WHERE `id` = 4;
UPDATE `tipo` SET `descricao` = 'Moda e Vestuário'         WHERE `id` = 5;
UPDATE `tipo` SET `descricao` = 'Equipamentos Eletrônicos' WHERE `id` = 6;
UPDATE `tipo` SET `descricao` = 'Instrumentos Musicais'    WHERE `id` = 7;
UPDATE `tipo` SET `descricao` = 'Raridades'                WHERE `id` = 8;
DELETE FROM `tipo` WHERE `id` = 9;   -- sobra da numeração deslocada; nenhum produto usa

-- ---------- textos com erro de digitação ----------
UPDATE `condicao`     SET `descricao` = 'Usado (Marcas de Uso)'      WHERE `id` = 4;
UPDATE `escolaridade` SET `formacao`  = 'Ensino Médio Incompleto'    WHERE `id` = 3;
UPDATE `escolaridade` SET `formacao`  = 'Ensino Superior Incompleto' WHERE `id` = 6;
