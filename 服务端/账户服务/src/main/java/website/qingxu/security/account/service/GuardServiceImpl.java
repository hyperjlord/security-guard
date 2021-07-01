package website.qingxu.security.account.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import website.qingxu.security.account.entity.Guard;
import website.qingxu.security.account.entity.GuardPK;


@Service
public class GuardServiceImpl implements GuardService{
    @Autowired
    private GuardRepository guardRepository;


    @Override
    public void addGuard(long guardian, long ward, String guardianName, String wardName){
        Guard guard = new Guard();
        guard.setWard(ward);
        guard.setGuardian(guardian);
        guard.setWardName(wardName);
        guard.setGuardianName(guardianName);
        guardRepository.save(guard);
    }

    @Override
    public void removeGuard(long guardian, long ward) {
        guardRepository.deleteById(new GuardPK(guardian, ward));
    }
}
