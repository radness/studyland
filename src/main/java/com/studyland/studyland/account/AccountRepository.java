package com.studyland.studyland.account;

import com.studyland.studyland.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

// readOnly true 설정하여 write 사용하지 않고 성능의 이점을 살린다.
@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account,Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Account findByEmail(String email);
}
