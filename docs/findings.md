> Ensaio executado: veja [resultados reais MTA 7.3.2](results-mta-7.3.2.md). A tabela abaixo é o mapa original de expectativas, não a contagem observada.

# Mapa de evidências para análise

Caminhos abaixo são relativos à raiz. Os arquivos Java ficam em `src/main/java/br/gov/mg/sef/demo/` em cada módulo.

| Padrão | Evidência | Expectativa/seleção | Remediação |
|---|---|---|---|
| Disco local fixo | PortalServlet: DIRETORIO, File, FileWriter | Candidato a cloud-readiness/local storage | Armazenamento durável com contrato e política de retenção |
| Disco no batch | ConciliacaoBatch: SAIDA, FileUtils | Candidato a cloud-readiness; cobertura de FileUtils pode variar | Object storage ou volume, conforme acesso/concorrência |
| IP e hostname fixos | CADASTRO_URL, JDBC_URL, SMTP_HOST | Candidato a hardcoded endpoint/IP; hostname genérico pode não gerar issue | Configuração externa, DNS de serviço e contratos |
| Sessão local | HttpSession, getSession, setAttribute | Candidato a cloud-readiness HTTP session | Stateless quando possível ou session store externo |
| Java EE antigo | javax.servlet.*, javax.mail.*, web.xml 3.1 | Jakarta/EAP 8; dependente do caminho escolhido | APIs Jakarta + runtime compatível + teste de deploy |
| Configuração embutida | ALIQUOTA, constantes e batch.properties | Revisão arquitetural; não há promessa de regra genérica | ConfigMap/config externa; Secrets apenas para segredos reais |
| Bibliotecas antigas | commons-lang 2.6, commons-io 2.4, mail 1.4.7 | Inventário de dependências; issue depende de regras/target/modo | Atualizar/substituir e testar; SCA em etapa própria |
| Acesso direto ao mainframe | MAINFRAME_URL nos dois Java | Regra custom sef-mainframe-direto-00001 em fonte | API institucional, política e desacoplamento real |
| Cálculo em double | PortalServlet.doPost | Discussão manual, sem regra prometida | BigDecimal e regras de arredondamento definidas |

Não prometer que todas as linhas viram issues na mesma análise. Um padrão pode ser classificado como tecnologia, insight, potencial issue ou não aparecer. O mapa reúne evidências para navegar no código mesmo quando a seleção de regras não as sinaliza.

Ensaio: salvar versão MTA, targets, modo, escopo, regras e relatório. Conferir pelo menos um finding nativo útil e o corporativo antes do evento. Se faltar um esperado, revisar logs, fonte versus binário, diretório, provider e ruleset; não inventar evidências ou contagem. Manter o relatório do ensaio para abrir durante a demonstração.
