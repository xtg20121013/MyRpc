package com.xtg.rpc.protocol;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString(of = {"header"})
public class Message implements Serializable {

    private Header header;

    private Object body;

}
