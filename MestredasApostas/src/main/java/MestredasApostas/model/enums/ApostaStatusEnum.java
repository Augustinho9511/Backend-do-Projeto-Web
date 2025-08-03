package MestredasApostas.model.enums;

import lombok.Getter;

@Getter
public enum ApostaStatusEnum {

    PENDENTE("Pendente"),
    GREEN("Green"),
    RED("Red"),
    CASHOUT("Cashout");

    ApostaStatusEnum(String cashout) {
    }
}
