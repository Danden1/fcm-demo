package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.EnumSet;
import java.util.Set;


//이 부분은 추후에 수정 필요할 듯. noficiationType에 값이 추가 되면, 코드를 수정해야함.
@Entity
@Getter
@NoArgsConstructor
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    //account가 아닌, 현재 notification이 주인인 상태임. 이를 수정할 필요 있음.
    @OneToOne
    @JoinColumn(name="account_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_id")
    @Enumerated(EnumType.STRING)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<NotificationType> availableNotification;

    public NotificationSettings(Account account){
        this.account = account;
        this.availableNotification = EnumSet.allOf(NotificationType.class);
        this.availableNotification.remove(NotificationType.TEST);
    }


    public void updateNotification(NotificationType notificationType, NotificationControl notificationControl){
        if(notificationControl== NotificationControl.ON){
            this.availableNotification.add(notificationType);
        }
        if(notificationControl == NotificationControl.OFF){
            this.availableNotification.remove(notificationType);
        }
    }


    public boolean isNotification(NotificationType notificationType){
        return availableNotification.contains(notificationType);
    }

}
