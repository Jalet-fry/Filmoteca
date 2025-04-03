package org.example.service.implementation;

import org.example.model.db.Actor;
import org.junit.jupiter.api.Test;

public class ActorServiceImplTest {
    private ActorServiceImpl actorServiceImpl;
    @Test
    void test() {
        Actor result = actorServiceImpl.get(1l);
    }
}
