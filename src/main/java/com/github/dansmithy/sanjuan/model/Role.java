package com.github.dansmithy.sanjuan.model;

public enum Role {

	GOVERNOR("governed"), BUILDER("built"), TRADER("traded"), PRODUCER("produced"), COUNCILLOR("counseled"), PROSPECTOR("prospected");

    private String pastTense;
    
    Role(String pastTense) {
        this.pastTense = pastTense;
    }

    public String getPastTense() {
        return pastTense;
    }
}
