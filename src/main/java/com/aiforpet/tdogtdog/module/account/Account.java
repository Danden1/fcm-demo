package com.aiforpet.tdogtdog.module.account;


import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String email;


    @Override
    public boolean equals(Object target){
        if(!(target instanceof Account)){
            return false;
        }
        return ((Account) target).getId() == this.id;
    }
}
