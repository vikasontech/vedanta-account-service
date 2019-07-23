/*
 *     Copyright (C) 2019  Vikas Kumar Verma
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.vedanta.vidiyalay.account_service.domain;

import lombok.*;
import org.hibernate.envers.Audited;
import org.vedanta.vidiyalay.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "account_master")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Audited
@ToString
@EqualsAndHashCode(callSuper = false)
public class AccountMasterEntity extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @Column(updatable = false)
    private Long enrolmentNo;
    private Date dateOfOpening;
    private BigDecimal totalFee;
    private BigDecimal dueAmount;
    @Column(updatable = false)
    private String accountType;
    @Column(columnDefinition = "DEFAULT 0")
    private BigDecimal totalFine;


}
