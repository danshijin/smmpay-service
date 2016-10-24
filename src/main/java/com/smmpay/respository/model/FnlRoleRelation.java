package com.smmpay.respository.model;

public class FnlRoleRelation {
    private Integer id;

    private Integer roleid;

    private String rolename;

    private String mainmenu;

    private String submenu;

    private String childmenu;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleid() {
        return roleid;
    }

    public void setRoleid(Integer roleid) {
        this.roleid = roleid;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename == null ? null : rolename.trim();
    }

    public String getMainmenu() {
        return mainmenu;
    }

    public void setMainmenu(String mainmenu) {
        this.mainmenu = mainmenu == null ? null : mainmenu.trim();
    }

    public String getSubmenu() {
        return submenu;
    }

    public void setSubmenu(String submenu) {
        this.submenu = submenu == null ? null : submenu.trim();
    }

    public String getChildmenu() {
        return childmenu;
    }

    public void setChildmenu(String childmenu) {
        this.childmenu = childmenu == null ? null : childmenu.trim();
    }
}