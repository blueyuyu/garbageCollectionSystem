package com.example.garbageCollection.service.impl;

import com.example.garbageCollection.entity.Garbage;
import com.example.garbageCollection.mapper.GarbageMapper;
import com.example.garbageCollection.service.IGarbageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2024-01-26
 */
@Service
public class GarbageServiceImpl extends ServiceImpl<GarbageMapper, Garbage> implements IGarbageService {

}
