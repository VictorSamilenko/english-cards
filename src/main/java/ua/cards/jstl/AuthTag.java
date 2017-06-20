package ua.cards.jstl;

import ua.cards.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class AuthTag extends TagSupport {

    protected Boolean authorized;
    protected Boolean admin;

    @Override
    public int doStartTag() throws JspException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        User user = (User) request.getAttribute("user");
        if (admin != null) {
            if (admin && user != null && user.isAdmin()){
                return EVAL_BODY_INCLUDE;
            } else {
                return SKIP_BODY;
            }
        }


        if (user == null){
            if (authorized){
                return SKIP_BODY;
            } else {
                return EVAL_BODY_INCLUDE;
            }
        } else {
            if(authorized){
                return EVAL_BODY_INCLUDE;
            } else {
                return SKIP_BODY;
            }
        }
    }

    public Boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Boolean authorized) {
        this.authorized = authorized;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
