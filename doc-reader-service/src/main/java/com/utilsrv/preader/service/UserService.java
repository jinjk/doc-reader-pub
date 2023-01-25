package com.utilsrv.preader.service;

import com.utilsrv.preader.jpa.entities.Person;
import com.utilsrv.preader.jpa.entities.UserIdTypeEnum;
import com.utilsrv.preader.jpa.repository.PersonRepository;
import com.utilsrv.preader.jpa.repository.UserIdTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Transactional
public class UserService {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    UserIdTypeRepository userIdTypeRepository;

    public Person findOrCreatePersonByDeviceId(String deviceId) {
        Person person = personRepository.findByUserIdAndType(deviceId, UserIdTypeEnum.DeviceId.getId());
        if (person == null) {
            LocalDateTime now = LocalDateTime.now();
            person = new Person();
            person.setCreatedDate(now);
            person.setUpdatedDate(now);
            person.setUserId(deviceId);
            person.setUserIdType(userIdTypeRepository.findById(UserIdTypeEnum.DeviceId.getId()).orElseThrow());
            personRepository.save(person);
        }
        return person;
    }
}
