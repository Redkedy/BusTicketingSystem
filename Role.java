package com.example.satisotomasyonu;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Role  implements UserActivity{
     private static ArrayList<Permission> perm;
     private  static int role_id;
     private String role_title;
     public Role()  {
         try {
             getPermissions();
         }
         catch (ThrowDialog th){

            System.exit(1);

         }

     }
     public Role(int role_id,String role_title,Permission[] permissions){




     }
      private void  getPermissions() throws ThrowDialog {

          this.perm= Login.getDb().getPermissions();

     }
    static ArrayList<Permission> getPermission(){
         return perm;
     }
    @Override
    public LocalDateTime getLastLogin() {
        return null;
    }

    @Override
    public Duration getTimeLoggedIn() {
        return null;
    }

    @Override
    public void setLastLogin(LocalDateTime lastLogin) {

    }
    public boolean addRole(int id,String role_title){


        return true;
    }
    public boolean edit(int id,String role_title){


        return true;
    }
    protected static int getRole_Id(){return role_id;}
    protected String getRole_title(){return this.role_title;}


}
