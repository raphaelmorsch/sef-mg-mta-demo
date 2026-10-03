package br.gov.mg.sef.demo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.math.BigDecimal;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.FileUtils;

/** Batch legado, executado uma vez. Não conecta a banco, SMTP ou mainframe. */
public class ConciliacaoBatch {
    private static final String SAIDA = "/tmp/sef-mg-demo/conciliacao/resumo.txt";
    private static final String JDBC_URL = "jdbc:oracle:thin:@192.0.2.30:1521:SEFDEMO";
    private static final String SMTP_HOST = "smtp.sef-demo.invalid";
    private static final String MAINFRAME_URL = "http://mainframe.sef-demo.invalid:8080/consulta";

    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        try (java.io.InputStream in = ConciliacaoBatch.class.getResourceAsStream("/batch.properties")) {
            if (in == null) throw new IllegalStateException("batch.properties ausente");
            config.load(in);
        }
        BigDecimal total = new BigDecimal("100.00").add(new BigDecimal("250.00"));
        String resumo = "DEMO SEM VALOR FISCAL\nregistros=2\ntotal=" + total + "\nunidade=" + config.getProperty("unidade") + "\n";
        FileUtils.writeStringToFile(new File(SAIDA), resumo, StandardCharsets.UTF_8.name());
        Properties mail = new Properties();
        mail.setProperty("mail.smtp.host", SMTP_HOST);
        MimeMessage aviso = new MimeMessage(Session.getInstance(mail)); // javax.mail legado.
        aviso.setSubject("Conciliação fictícia", "UTF-8");
        aviso.setText(resumo, "UTF-8"); // Não chama Transport.send().
        System.out.println(resumo + "Arquivo local: " + SAIDA);
        System.out.println("Destinos apenas configurados: " + JDBC_URL + " | " + MAINFRAME_URL);
        System.out.println("Aviso criado em memória: " + aviso.getSubject());
    }
}
