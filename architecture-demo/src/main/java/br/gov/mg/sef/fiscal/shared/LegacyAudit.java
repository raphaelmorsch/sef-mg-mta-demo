package br.gov.mg.sef.fiscal.shared;

public final class LegacyAudit {
    private LegacyAudit() {}

    public static void registrar(String mensagem) {
        System.out.println("[AUDITORIA DEMO] " + mensagem);
    }
}
