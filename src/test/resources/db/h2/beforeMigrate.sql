-- Callback do Flyway (só nos testes, perfil "test"): antes das migrações, ensina ao H2 as
-- funções do MySQL que elas usam. As migrações em si não mudam (já foram aplicadas no MySQL).
CREATE ALIAS IF NOT EXISTS "timestamp" FOR 'com.sigl.sigl.h2.FuncoesMySql.timestamp';
