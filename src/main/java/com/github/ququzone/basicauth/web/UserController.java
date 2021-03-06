package com.github.ququzone.basicauth.web;

import com.github.ququzone.basicauth.model.ResourceMapping;
import com.github.ququzone.basicauth.model.Role;
import com.github.ququzone.basicauth.model.UserVO;
import com.github.ququzone.basicauth.service.AuthService;
import com.github.ququzone.common.GsonUtil;
import com.github.ququzone.common.Page;
import com.github.ququzone.common.ServiceException;
import com.github.ququzone.common.web.FlashMessage;
import com.github.ququzone.common.web.JsonResult;
import com.google.gson.annotations.Expose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * user controller.
 *
 * @author Yang XuePing
 */
@Controller
public class UserController {
    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public
    @ResponseBody
    String user(HttpServletRequest request) {
        String userId = (String) request.getSession().getAttribute("user");
        return GsonUtil.DEFAULT_GSON.toJson(authService.getUserVO(userId));
    }

    @RequestMapping(value = "/nav", method = {RequestMethod.GET, RequestMethod.POST})
    public String nav(HttpServletRequest request) {
        String userId = (String) request.getSession().getAttribute("user");
        request.setAttribute("user", authService.getUserVO(userId));
        request.setAttribute("menus", authService.getUserMenus(userId));
        return "include/nav";
    }

    @RequestMapping(value = "/user/settings", method = RequestMethod.GET)
    public String settings(HttpServletRequest request) {
        request.setAttribute("user", authService.getUserVO((String) request.getSession().getAttribute("user")));
        return "auth/settings";
    }

    @RequestMapping(value = "/user/settings", method = RequestMethod.POST)
    public String doSettings(
            HttpServletRequest request,
            @RequestParam(name = "display_name") String displayName,
            @RequestParam(name = "change_password", required = false) boolean changePassword,
            @RequestParam(name = "origin_password", required = false) String originPassword,
            @RequestParam(name = "password", required = false) String password,
            @RequestParam(name = "repassword", required = false) String repassword) {
        if (displayName == null || displayName.isEmpty()) {
            request.setAttribute("flashMessage", new FlashMessage(FlashMessage.Level.error, "姓名不能为空"));
            request.setAttribute("user", authService.getUserVO((String) request.getSession().getAttribute("user")));
            return "auth/settings";
        }
        if (changePassword) {
            if (originPassword == null || originPassword.isEmpty()) {
                request.setAttribute("flashMessage", new FlashMessage(FlashMessage.Level.error, "原密码不能为空"));
                request.setAttribute("user", authService.getUserVO((String) request.getSession().getAttribute("user")));
                return "auth/settings";
            }
            if (password == null || password.isEmpty()) {
                request.setAttribute("flashMessage", new FlashMessage(FlashMessage.Level.error, "密码不能为空"));
                request.setAttribute("user", authService.getUserVO((String) request.getSession().getAttribute("user")));
                return "auth/settings";
            }
            if (repassword == null || repassword.isEmpty()) {
                request.setAttribute("flashMessage", new FlashMessage(FlashMessage.Level.error, "重复密码不能为空"));
                request.setAttribute("user", authService.getUserVO((String) request.getSession().getAttribute("user")));
                return "auth/settings";
            }
            if (!password.equals(repassword)) {
                request.setAttribute("flashMessage", new FlashMessage(FlashMessage.Level.error, "两次输入密码不一致"));
                request.setAttribute("user", authService.getUserVO((String) request.getSession().getAttribute("user")));
                return "auth/settings";
            }
        }
        try {
            authService.settingUser((String) request.getSession().getAttribute("user"), displayName, changePassword, originPassword, password);
        } catch (ServiceException e) {
            request.setAttribute("flashMessage", new FlashMessage(FlashMessage.Level.error, e.getMessage()));
            request.setAttribute("user", authService.getUserVO((String) request.getSession().getAttribute("user")));
            return "auth/settings";
        }
        return "redirect:/user/settings";
    }

    @ResourceMapping(name = "用户管理", pattern = "/users", method = ResourceMapping.RequestMethod.GET)
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String users(HttpServletRequest request,
                        @RequestParam(value = "page", required = false, defaultValue = "1") int page) {
        request.setAttribute("users", authService.userPage(page, Page.DEFAULT_PAGE_SIZE));
        return "auth/users";
    }

    @ResourceMapping(name = "用户管理_新增用户", pattern = "/user", method = ResourceMapping.RequestMethod.POST)
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<String> add(@RequestParam("username") String username,
                                      @RequestParam("display_name") String displayName,
                                      @RequestParam("password") String password) {
        try {
            authService.addUser(username, displayName, password);
            return ResponseEntity.ok("{}");
        } catch (ServiceException e) {
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()).toString());
        }
    }

    @ResourceMapping(name = "用户管理_查看用户", pattern = "/user/{id}", method = ResourceMapping.RequestMethod.GET)
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> get(@PathVariable("id") String id) {
        UserVO user = authService.getUserVO(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(GsonUtil.DEFAULT_GSON.toJson(user));
    }

    @ResourceMapping(name = "用户管理_编辑用户", pattern = "/user/{id}", method = ResourceMapping.RequestMethod.PUT)
    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> update(@PathVariable("id") String id, @RequestParam("username") String username,
                                         @RequestParam("display_name") String displayName,
                                         @RequestParam("password") String password) {
        try {
            authService.updateUser(id, username, displayName, password);
            return ResponseEntity.ok("{}");
        } catch (ServiceException e) {
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()).toString());
        }
    }

    @ResourceMapping(name = "用户管理_禁用用户", pattern = "/user/{id}/disable", method = ResourceMapping.RequestMethod.POST)
    @RequestMapping(value = "/user/{id}/disable", method = RequestMethod.POST)
    public ResponseEntity<String> disable(@PathVariable("id") String id) {
        authService.disableUser(id);
        return ResponseEntity.ok(null);
    }

    @ResourceMapping(name = "用户管理_启用用户", pattern = "/user/{id}/enable", method = ResourceMapping.RequestMethod.POST)
    @RequestMapping(value = "/user/{id}/enable", method = RequestMethod.POST)
    public ResponseEntity<String> enable(@PathVariable("id") String id) {
        authService.enableUser(id);
        return ResponseEntity.ok(null);
    }

    @ResourceMapping(name = "用户管理_查看用户角色", pattern = "/user/{id}/roles", method = ResourceMapping.RequestMethod.GET)
    @RequestMapping(value = "/user/{id}/roles", method = RequestMethod.GET)
    public ResponseEntity<String> roles(@PathVariable("id") String id) {
        List<Role> all = authService.roles();
        List<Role> userRoles = authService.userRoles(id);
        List<UserRole> result = all.stream().map(role -> {
            UserRole userRole = new UserRole();
            userRole.setId(role.getId());
            userRole.setName(role.getName());
            userRole.setCreatedTime(role.getCreatedTime());
            userRole.setUpdatedTime(role.getUpdatedTime());
            for (Role ur : userRoles) {
                if (ur.getId().equals(role.getId())) {
                    userRole.setChecked(true);
                    break;
                }
            }
            return userRole;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(GsonUtil.DEFAULT_GSON.toJson(result));
    }

    @ResourceMapping(name = "用户管理_分配用户角色", pattern = "/user/{id}/roles", method = ResourceMapping.RequestMethod.POST)
    @RequestMapping(value = "/user/{id}/roles", method = RequestMethod.POST)
    public ResponseEntity<String> roles(@PathVariable("id") String id, @RequestParam("roles") String[] roles) {
        authService.assignUserRole(id, roles);
        return ResponseEntity.ok(null);
    }

    private static class UserRole extends Role {
        @Expose
        private boolean checked;

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }
    }
}
