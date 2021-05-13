package com.fno.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fno
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
