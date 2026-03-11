package com.dongyang.foodpage.service;

import com.dongyang.foodpage.dao.MemberDAO;
import com.dongyang.foodpage.dto.MemberDTO;

import java.util.List;

public class MemberService {
    private final MemberDAO dao = new MemberDAO();

    // 일반 회원가입 (회원 스스로 가입)
    public boolean register(MemberDTO member) {
        if (member.getRole() == null || member.getRole().isBlank()) {
            member.setRole("USER"); // 기본 권한 USER
        }
        return dao.insert(member); // 기본 insert (id, pw, name, role)
    }

    // 관리자 회원 추가 (모든 필드 저장)
    public boolean addMember(MemberDTO member) {
        if (member.getRole() == null || member.getRole().isBlank()) {
            member.setRole("USER"); // 기본 권한 USER
        }
        return dao.addMember(member); // 확장된 insert (id, pw, name, role, allergy, comp, home)
    }

    public MemberDTO login(String id, String pw) {
        MemberDTO member = dao.findById(id);
        if (member != null) {
            System.out.println("DB에서 가져온 pw: " + member.getPw());
            System.out.println("입력한 pw: " + pw);
            if (member.getPw() != null && member.getPw().equals(pw)) {
                return member;
            } else {
                System.out.println("비밀번호 불일치 또는 null");
            }
        } else {
            System.out.println("해당 ID의 회원 없음");
        }
        return null;
    }

    public MemberDTO getMember(String id) {
    	MemberDAO dao = new MemberDAO();
        return dao.findById(id);
    }

    public boolean updateProfile(String id, String newName, String newPw,
                                 String newAllergy, String newComp, String newHome) {
        return dao.updateProfile(id, newName, newPw, newAllergy, newComp, newHome);
    }

    public List<MemberDTO> listAll() {
        return dao.findAll();
    }

    public boolean delete(String id) {
        return dao.deleteById(id);
    }
}