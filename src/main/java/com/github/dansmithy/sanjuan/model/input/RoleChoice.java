package com.github.dansmithy.sanjuan.model.input;

import com.github.dansmithy.sanjuan.model.Role;

public class RoleChoice {

	private Role role;
    private boolean useLibrary = true;

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

    public boolean isUseLibrary() {
        return useLibrary;
    }

    public void setUseLibrary(boolean useLibrary) {
        this.useLibrary = useLibrary;
    }
}
