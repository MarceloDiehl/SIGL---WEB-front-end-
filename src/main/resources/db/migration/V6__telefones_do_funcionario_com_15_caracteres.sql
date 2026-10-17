-- O cadastro de funcionário valida o telefone no formato "(51) 99812-4410" (15 caracteres),
-- mas as colunas tinham 14: todo cadastro com telefone válido estourava o tamanho.
-- Mesmo tamanho do telefone do cliente e do mapeamento da entidade (length = 15).
ALTER TABLE `cadastrofuncionario` MODIFY `telefone` VARCHAR(15) DEFAULT NULL;
ALTER TABLE `cadastrofuncionario` MODIFY `contatoEmergencia` VARCHAR(15) NOT NULL;
