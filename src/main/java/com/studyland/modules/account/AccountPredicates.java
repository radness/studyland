package com.studyland.account;

import com.querydsl.core.types.Predicate;
import com.studyland.domain.QAccount;
import com.studyland.domain.Tag;
import com.studyland.domain.Zone;

import java.util.Set;

public class AccountPredicates {

    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
}
