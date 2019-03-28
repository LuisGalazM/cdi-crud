package com.cdi.crud.infra.security;

import com.cdi.crud.infra.exception.CustomException;
import com.github.adminfaces.template.session.AdminSession;
import org.apache.deltaspike.security.api.authorization.Secures;
import org.omnifaces.util.Messages;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rmpestano on 12/20/14.
 */
@SessionScoped
@Named("authorizer")
public class CustomAuthorizer implements Serializable {

    private Map<String, String> currentUser = new ConcurrentHashMap<>();


    @Secures
    @Admin
    public boolean doAdminCheck(InvocationContext invocationContext, BeanManager manager) throws Exception {
        boolean allowed = currentUser.containsKey("user") && currentUser.get("user").equals("admin");
        if(!allowed){
            throw new CustomException("Access denied");
        }
        return allowed;
    }

    @Secures
    @Guest
    public boolean doGuestCheck(InvocationContext invocationContext, BeanManager manager) throws Exception {
        boolean allowed = currentUser.containsKey("user") && currentUser.get("user").equals("guest") || doAdminCheck(null, null);
        if(!allowed){
            throw new CustomException("Access denied");
        }
        return allowed;
    }

    public void login(String username) {
        currentUser.put("user", username);
        if(FacesContext.getCurrentInstance() != null){
        	Messages.addInfo(null, "Logged in sucessfully as <b>"+username+"</b>");
        }
    }

    public Map<String, String> getCurrentUser() {
        return currentUser;
    }
}
