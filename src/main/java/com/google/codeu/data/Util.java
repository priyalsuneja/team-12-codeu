/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.codeu.data;

/**
 *
 * @author 018639476
 */
public final class Util {
    private Util(){}
    public static boolean isValidCharityUser(User locationUser)
    {
      return locationUser!=null && locationUser.getType()!=null && locationUser.getType()== User.CHARITY_TYPE;
    }
}
