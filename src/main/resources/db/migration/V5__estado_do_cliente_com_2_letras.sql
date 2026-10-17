-- UF do cliente era CHAR(45); a de funcionário já era CHAR(2). Todos os valores têm 2 letras.
-- Divergência achada pela validação do Hibernate (ddl-auto=validate) no perfil de testes.
ALTER TABLE `cadastrocliente` MODIFY `estado` CHAR(2) NOT NULL;
