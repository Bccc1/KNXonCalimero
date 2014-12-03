package com.calimero.knx.knxoncalimero;

/**
 * Created by David on 26.11.2014.
 *
 * Das sollte später alles anders, ist nur ein Prototyp.
 * Gedanke ist, dass eine KNXAction eine auszuführende Aktion ist,
 * wie zum Beispiel ein Telegram "an" an die Gruppenadresse von Licht.
 * So eine Action kann selber sich nicht ausführen sondern ist nur ein Container für die Daten.
 * Für die Ausführung bräuchte es eine weiter Klasse, aktuell der {@link KnxAdapter}.
 *
 * Dass das alles häßlich ist, weiß ich auch - Mir passte das so aber grad ganz gut in den Prototyp.
 *
 */
public class KnxAction {

    /* Id um die Action in einer Datenbank zu speichern */
    String id;

    /* Ein Name für Darstellungszwecke */
    String name;

    /* Das ist klar, oder? */
    String gruppenadresse;

    /* Das Zeugs was im Telegram bauch ist, kA wie das heißt. Dass "an" in "Licht an!" halt. */
    String daten;

    public KnxAction() {
    }

    public KnxAction(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) { //FIXME ignoriert noch die ID
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KnxAction knxAction = (KnxAction) o;

        if (daten != null ? !daten.equals(knxAction.daten) : knxAction.daten != null) return false;
        if (gruppenadresse != null ? !gruppenadresse.equals(knxAction.gruppenadresse) : knxAction.gruppenadresse != null)
            return false;
        if (name != null ? !name.equals(knxAction.name) : knxAction.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (gruppenadresse != null ? gruppenadresse.hashCode() : 0);
        result = 31 * result + (daten != null ? daten.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
