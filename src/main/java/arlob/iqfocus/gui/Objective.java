package arlob.iqfocus.gui;

public class Objective {
    private String challengeString;
    private String initialState;

    public Objective(String challengeString, String initialState) {
        this.challengeString = challengeString;
        this.initialState = initialState;
    }

    public Objective returnObjective() {
        return new Objective("", "");
    }

    public String getChallengeString() { return challengeString; }
    public String getInitialState() { return initialState; }
}
