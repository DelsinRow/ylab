package com.sinaev.services;

import com.sinaev.models.dto.UserDTO;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;


public interface UserService {
    void login(HttpServletRequest httpReq, UserDTO userDTO);
    void register(UserDTO userDTO);
    void setUserDTOInSession(HttpServletRequest req, UserDTO userDTO);
}
