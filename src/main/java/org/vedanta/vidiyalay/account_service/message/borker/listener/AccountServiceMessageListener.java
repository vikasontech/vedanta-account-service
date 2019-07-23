/*
 * Copyright (C) 2019  Vikas Kumar Verma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.vedanta.vidiyalay.account_service.message.borker.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.vedanta.vidiyalay.account_service.facade.AccountFacade;
import org.vedanta.vidiyalay.account_service.message.borker.PatternTopics;
import org.vedanta.vidiyalay.account_service.web.rest.vm.StudentNewAdmissionVM;

@Component
@Slf4j
public class AccountServiceMessageListener implements MessageListener {
    private final AccountFacade accountFacade;
    private final RestTemplate restTemplate;

    AccountServiceMessageListener(AccountFacade accountFacade, RestTemplate restTemplate) {
        this.accountFacade = accountFacade;
        this.restTemplate = restTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        final String patternName = new String(pattern);

        if (!(patternName.equals(PatternTopics.STUDENT_CREATED.name()))) {
            log.warn("Unhandled Message for \npattern: {} \nmessage body: {}", new String(pattern), new String(message.getBody()));
            return;
        }

        log.info("creating account master data");
        accountFacade.createAccountFromStudentAdmissionDetails(new Jackson2JsonRedisSerializer<>(StudentNewAdmissionVM.class).deserialize(message.getBody()));
    }

}
