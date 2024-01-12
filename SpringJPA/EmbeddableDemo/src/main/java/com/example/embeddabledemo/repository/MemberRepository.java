package com.example.embeddabledemo.repository;

import com.example.embeddabledemo.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
