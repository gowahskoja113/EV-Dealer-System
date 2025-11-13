
package com.swp391.evdealersystem.dto.request;

import lombok.*;
import com.swp391.evdealersystem.enums.AppointmentStatus;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UpdateAppointmentStatusRequest {
    private AppointmentStatus status; // SCHEDULED, IN_SERVICE, COMPLETED, CANCELED
    private String note;


}
