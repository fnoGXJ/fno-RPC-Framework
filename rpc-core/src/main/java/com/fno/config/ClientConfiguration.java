package com.fno.config;

import com.fno.rpc.balance.LoadBalance;
import com.fno.rpc.serializer.Serializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ClientConfiguration {
    private LoadBalance loadBalance;
    private Serializer serializer;
}
