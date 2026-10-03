package br.gov.mg.sef.demo;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;

/** Legado intencional. Todos os dados e endereços são fictícios. */
public class PortalServlet extends HttpServlet {
    // MTA: local-storage, hardcoded endpoints; regra corporativa: acesso direto ao mainframe.
    private static final String DIRETORIO = "/tmp/sef-mg-demo/recibos";
    private static final String CADASTRO_URL = "http://192.0.2.20:8080/cadastro";
    private static final String MAINFRAME_URL = "http://mainframe.sef-demo.invalid:8080/consulta";
    private static final double ALIQUOTA = 0.18;

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        HttpSession session = req.getSession();
        Integer visitas = (Integer) session.getAttribute("visitas");
        visitas = visitas == null ? 1 : visitas + 1;
        session.setAttribute("visitas", visitas); // Estado preso à JVM; desaparece no restart.
        String recibo = (String) session.getAttribute("ultimoRecibo");
        resp.getWriter().printf("<!doctype html><html lang='pt-BR'><meta charset='UTF-8'>"
            + "<title>SEF-MG | laboratório fictício</title><body>"
            + "<h1>Portal de arrecadação — legado</h1><p>Laboratório fictício, sem dados reais.</p>"
            + "<p>Visitas nesta sessão: %d</p><p>Último recibo: %s</p>"
            + "<form method='post'><label>Valor base (R$): <input name='valor' value='100.00'></label>"
            + "<button>Emitir recibo fictício</button></form>"
            + "<p>Alíquota embutida: %.0f%%</p><p>Cadastro configurado: %s</p>"
            + "<p>Integração direta: %s (simulada; sem chamada de rede)</p></body></html>",
            visitas, recibo == null ? "nenhum" : recibo, ALIQUOTA * 100, CADASTRO_URL, MAINFRAME_URL);
    }

    @Override protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String valor = StringUtils.trimToEmpty(req.getParameter("valor")); // Biblioteca antiga em uso real.
        double base;
        try { base = Double.parseDouble(valor); }
        catch (NumberFormatException e) { resp.sendError(400, "Informe um valor numérico."); return; }
        if (!Double.isFinite(base) || base <= 0 || base > 1000000) { resp.sendError(400, "Valor fora da faixa da demo."); return; }
        File dir = new File(DIRETORIO);
        if (!dir.isDirectory() && !dir.mkdirs()) { throw new IOException("Não foi possível criar " + DIRETORIO); }
        File recibo = File.createTempFile("recibo-", ".txt", dir);
        try (FileWriter writer = new FileWriter(recibo)) {
            writer.write(String.format(java.util.Locale.ROOT, "DEMO SEM VALOR FISCAL\nbase=%.2f\nimposto=%.2f\n", base, base * ALIQUOTA));
        }
        req.getSession().setAttribute("ultimoRecibo", recibo.getName());
        resp.sendRedirect(req.getContextPath() + "/");
    }
}
