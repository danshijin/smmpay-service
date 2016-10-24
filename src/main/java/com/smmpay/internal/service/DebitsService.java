package com.smmpay.internal.service;

import com.smmpay.inter.dto.req.PlatformDebitsDTO;
import com.smmpay.inter.dto.res.ReturnDTO;

/**
 * Created by tangshulei on 2015/12/4.
 */
public interface DebitsService {

    ReturnDTO debits(PlatformDebitsDTO platformDebitsDTO, ReturnDTO returnDTO);
}
