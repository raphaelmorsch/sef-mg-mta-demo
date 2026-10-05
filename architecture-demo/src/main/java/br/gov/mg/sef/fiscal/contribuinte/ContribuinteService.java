package br.gov.mg.sef.fiscal.contribuinte;

import br.gov.mg.sef.fiscal.debito.DebitoService;
import br.gov.mg.sef.fiscal.shared.LegacyAudit;

public class ContribuinteService {
    public void regularizar(Contribuinte contribuinte) {
        LegacyAudit.registrar("Regularizando " + contribuinte.nome());
        new DebitoService().quitar(contribuinte.nome());
    }

    public static void main(String[] args) {
        new ContribuinteService().regularizar(new Contribuinte("Empresa de exemplo"));
    }
}
