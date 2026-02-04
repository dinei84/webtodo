package com.todo.security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Representa o usuário autenticado no contexto do Spring Security.
 */
@Data
@AllArgsConstructor
public class UserPrincipal {
    private String uid;
    private String email;
}
