package com.oweu.authorizationserver.controller;

import com.oweu.authorizationserver.oridinary.Entity.resultRender;
import com.oweu.authorizationserver.oridinary.util.GetIPAddress;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import com.oweu.authorizationserver.oridinary.constant.SystemConstant;
import com.oweu.authorizationserver.oridinary.Entity.R;
import com.oweu.authorizationserver.oridinary.util.JwtUtils;
import com.oweu.authorizationserver.model.User;
import com.oweu.authorizationserver.repository.UserRepository;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


@Api(tags = "UserManagement")
@Controller

public class AuthController {
    @Autowired
    UserRepository userRepository;


    @ApiOperation(value = "User Login")
    @CrossOrigin
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView login(String username, String password, RedirectAttributes attr,
                              HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findByUsername(username);
        String addr = GetIPAddress.getIpAddress(request);
        String URI = request.getRequestURI();
        addr += URI;
        /* Can't find the username or
        the user types the wrong password
        The boolean variable match indicate the situations.
         */
        boolean match = true;

        if (user != null) {
            String truePassword = user.getPassword();
            if (!truePassword.equals(password))
                match = false;
            else {
                match = true;
            }
        } else {
            match = false;
        }
        if (!match) {
            attr.addAttribute("msg", "Something wrong!");
            return new ModelAndView("redirect:" + addr);
        }
        /*The username and password match!* Redirect to the service*/
        else {
            //Send back the token to client
            String JWT = JwtUtils.createJWT("1", username, SystemConstant.JWT_TTL);
            attr.addAttribute("token", JWT);
            return new ModelAndView("redirect:" + addr);
        }
    }

    @ApiOperation(value = "get User info")
    @CrossOrigin
    @RequestMapping(value = "description", method = RequestMethod.POST)
    public R description(String username) {
        User user = userRepository.findByUsername(username);
        return R.ok(user.getDescription());
    }

    @ApiOperation(value = "Check the Login State")
    @CrossOrigin
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ModelAndView checkin(String token, HttpServletRequest request, ModelMap model) {
        String addr = GetIPAddress.getIpAddress(request);
        token = request.getParameter("token");
        if (token.equals("")) {
            return new ModelAndView(new RedirectView(addr + "/login"));
        } else {
            resultRender resultRender = null;
            resultRender = JwtUtils.validateJWT(token);
            if (resultRender.isSuccess()) {
                model.put("msg", "Authorized");
                return new ModelAndView(new RedirectView(addr + "/wordladder"), model);
            }
            switch (resultRender.getErrCode()) {
                // Token Authorizing wrong
                case SystemConstant.JWT_ERRCODE_FAIL:
                    return new ModelAndView(new RedirectView(addr + "/login"));
                // Toke is out of state
                case SystemConstant.JWT_ERRCODE_EXPIRE:
                    return new ModelAndView(new RedirectView(addr + "/login"));
                default:
                    break;
            }
            return new ModelAndView("redirect:" + addr + "/login");
        }
    }
}

