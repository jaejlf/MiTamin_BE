# 마음 면역력을 키우는 멘탈케어 서비스, 마이타민

![image](https://user-images.githubusercontent.com/78673570/203768892-848cd6b3-1348-4b2c-bb0a-cde5bf897864.png)

## 💊 프로젝트 소개

"아픈 상처에도 쉽게 무너지지 않도록", 마음에도 면역력이 필요하다는 사실, 알고 계신가요? 마이타민은 매일매일 꾸준히 챙겨 먹는 비타민 같은 서비스가 되어 여러분의 마음 면역력을 키우는데 도움이 되는 것을 목표로 하고 있습니다.

<br>

핵심 기능은 다음과 같습니다.

1. 하루에 ‘한 번만’ 기록할 수 있는 마음 관리 루틴 (숨 고르기 → 감각 깨우기 → 하루 진단 → 칭찬 처방)
2. 월간/주간 감정 랭킹 및 조건에 따른 통계 기록 조회
3. 한 달에 한 번 나를 위한 행동을 한 후, 이미지와 함께 기록하는 데이노트

<br><br>

## 🔗 Download
- Play Store. https://bit.ly/mitamin_aos 
- App  Store. https://bit.ly/mitamin_ios

<br><br>

## 🛠 기술 스택

- `Spring Boot(Java)` 를 사용하여 API 를 개발하였고, `JPA` 와 `QueryDSL` 기술을 사용하였습니다.
- 데이터베이스로는 `AWS RDS(MySQL)` 를 사용하였고, `AWS EC2` 를 통해 서버 배포를 진행하였습니다.
- 이미지 업로드를 위해 `AWS S3` 버킷을 사용하였습니다.
- 협업 및 코드 버전 관리를 위해 `Github` 및 `Jira`, API 문서 관리를 위해 `Spring Rest Docs` 를 사용하였습니다.
- `Nginx` 를 통해 무중단 배포 환경을 구성하였습니다.

<br><br>

## 👩‍💻 개발 내용
- 마음 관리 루틴에 따른 기록 (숨 고르기 → 감각 깨우기 → 하루 진단 → 칭찬 처방)
- 조건에 따른 칭찬 처방 기록 필터링 검색
- 새로고침 시마다 칭찬 처방 기록 랜덤 조회
- 데이노트 관련 기능 개발(= 이미지와 함께 쓰는 일기)
- 이번 달의 감정 랭킹 TOP3 제공
- 월간/감정 통계 기록 조회
- 사용자 설정에 따른 개별/전체 푸시 알림

<br><br>

## 📑 문서

- [API Documentation] [mitamin-apis](https://jaejlf.github.io/MiTamin_BE/)

<br><br>

## 🧩 서비스 아키텍처
![image](https://user-images.githubusercontent.com/78673570/201472540-1b60ba08-e610-43b0-b8c2-ca75e15228a2.png)

<br><br>

## 💾 ERD Diagram
![image](https://user-images.githubusercontent.com/78673570/202361366-a5c99778-a238-47f5-8b4c-fc0f9b4849b2.png)

<br><br>

## 🏷 Release Version
- [sprint/1](https://github.com/jaejlf/MyTamin_BE/releases/tag/v1.0.0) - 홈화면, 마이타민 섭취 기능 구현
- [sprint/2](https://github.com/jaejlf/MyTamin_BE/releases/tag/v1.0.1) - 유저 관련 기능, 데이노트 및 위시 리스트 기능 구현
- [sprint/3](https://github.com/jaejlf/MyTamin_BE/releases/tag/v1.0.2) - 계정 관리 기능, 히스토리 기능 및 월간/주간 기록 통계 정보 제공 기능 구현
- [sprint/4](https://github.com/jaejlf/MiTamin_BE/releases/tag/v1.0.4) - 푸시 알림 기능, 성능 개선 및 리팩토링, 기획 변경에 따른 세부 기능 구현

<br><br>

## 🔍 화면 구성
![image](https://user-images.githubusercontent.com/78673570/201472564-75af011f-3123-43d1-ab0f-3a0c470cd412.png)
