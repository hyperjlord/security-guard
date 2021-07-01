package website.qingxu.security.account.service;

public interface GuardService {
    void addGuard(long guardian, long ward, String guardianName, String wardName);
    void removeGuard(long guardian, long ward);
}
