package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;


//이 부분은 추후에 수정 필요할 듯. noficiationType에 값이 추가 되면, 코드를 수정해야함.
@Entity
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    //account가 아닌, 현재 notification이 주인인 상태임. 이를 수정할 필요 있음.
    @OneToOne
    @JoinColumn(name="account_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    @Column(nullable = false)
    private Boolean important;

    @Column(nullable = false)
    private Boolean event;

    @Column(nullable = false)
    private Boolean test;


    public Notification(Account account){
        this.account = account;
        this.important = true;
        this.event = true;
        this.test = false;
    }

    public void updateNotification(NotificationType notificationType, NotificationSetting notificationSetting){
        if(notificationType == NotificationType.IMPORTANT){
            this.important = notificationSetting.getValue();
        }
        else if(notificationType == NotificationType.EVENT){
            this.event = notificationSetting.getValue();
        }
        else if(notificationType == NotificationType.TEST){
            this.test = notificationSetting.getValue();
        }
    }


    public boolean isNotification(NotificationType notificationType){
        if(notificationType == NotificationType.IMPORTANT){
            return this.important;
        }
        else if(notificationType == NotificationType.EVENT){
            return this.event;
        }
        else if(notificationType == NotificationType.TEST){
            return this.test;
        }
        return false;
    }

}
