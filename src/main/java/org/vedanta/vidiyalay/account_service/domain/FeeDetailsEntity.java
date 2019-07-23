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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Getter
@Builder
@Entity
@Table(name="fee_details")
@Audited
@AllArgsConstructor
@NoArgsConstructor
public class FeeDetailsEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    private int standard;
    private int year;
    private String description;
    private BigDecimal amount;
    private Boolean isActive;
}
