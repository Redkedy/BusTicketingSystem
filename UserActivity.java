package com.example.satisotomasyonu;
import java.time.Duration;
import java.time.LocalDateTime;

public  interface UserActivity {
     LocalDateTime lastLogin = null;
     Duration duration = null;



    LocalDateTime getLastLogin() ;
    public Duration getTimeLoggedIn();

    public void setLastLogin(LocalDateTime lastLogin);


}
