package MestredasApostas.reponse;


public class ApiSportsPredictionResponse {

    private String resultado;
    private String gols;
    private String ambasMarcam;
    private String cartoes;
    private String escanteios;

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getGols() { return gols; }
    public void setGols(String gols) { this.gols = gols; }

    public String getAmbasMarcam() { return ambasMarcam; }
    public void setAmbasMarcam(String ambasMarcam) { this.ambasMarcam = ambasMarcam; }
}
