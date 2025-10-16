A) Attendance & Workflow (BR-01 … BR-26)
BR-01 Managers can access attendance data only for employees in their own departments.


BR-02 HR/HRM can view all departments.


BR-03 When a period is Locked, all records in that period are read-only for managers.


BR-04 Team Attendance requires a selected date range or period; the date range must be ≤ 31 days.


BR-05 [CHANGED] WorkHours = Check-out − Check-in − UnpaidBreaksOnly − Late/Early deduction. If a continuous day shift ≥ 6h, provide a paid break ≥ 30 minutes that counts as working time (i.e., not deducted). Thư Viện Nhà Đất


BR-06 Rounding: check-in rounds down, check-out rounds up; rounding unit is configurable (RoundingUnitMinutes).


BR-07 Grace/Tolerance before marking Late/Early: ToleranceMinutes (default 5).


BR-08 OTHours = max(0, WorkHours − ShiftHours); apply multipliers per BR-OT-01.


BR-09 Status precedence: Approved Leave overrides Absent/Missing/Late/Early.


BR-10 Adjustments allowed only in Unlocked periods and must not overlap approved leave (unless explicit HR override).


BR-11 Approving an adjustment recalculates Work/OT and stores approver, timestamp, note.


BR-12 Rejecting an adjustment requires a reason.


BR-13 A period cannot be submitted to HR if any adjustments are pending.


BR-14 Submitting to HR sets status Submitted; managers become read-only on those records.


BR-15 Only HR may Lock; Unlock requires HRM authorization plus a reason (audit-logged).


BR-16 Generate Payroll only for Locked periods.


BR-17 Post-payroll attendance changes flag payroll as Dirty; must Regenerate before payment.


BR-18 Export honors current filters; includes totals: Work, OT, Late, Absent.


BR-19 Managers are notified on new adjustments; overdue items escalate to HR per configured SLA.


BR-20 Approve/Submit buttons disabled until prerequisites are met; show tooltip explaining missing prerequisites.


BR-21 Managers cannot edit check-in/out directly; all changes via approved adjustments.


BR-22 Full-day approved leave → WorkHours = 0, no OT, no Absent entry.


BR-23 Half-day leave uses HalfDayHours (default: 4 hours); suppress Late/Early flags within the leave window. Morning leave (08:00–12:00) suppresses late; afternoon leave (13:00–17:00) suppresses early. Outside that window, normal rules apply (BR-06/07). Half-day leave can only be requested for working days, not weekends/holidays.


BR-24 View/approve/submit/lock/unlock operations are fully audit-logged (who/when/before–after values).


BR-25 Submitting/locking a period triggers notifications to relevant managers and HR.


BR-26 Only the Dept Manager role may approve adjustments for their teams.


B) Additional Limits (BR-AD — day shift only; company caps)
BR-AD-01 Day-shift only: block 22:00–06:00 (no night work features enabled).


BR-AD-02 Company cap: Daily total (regular + OT) ≤ 10h; weekly total ≤ 48h.
 Note: the Labor Code allows up to 10h/day (incl. OT), ≤ 40h/month OT, and typically ≤200h/year (some sectors up to 300h); we keep 10h/day as a stricter company policy. Vietnam Briefing+2aseanbriefing.com+2


BR-AD-03 Monthly OT cap ≤ 40h (includes weekends/holidays). Vietnam Briefing


BR-AD-04 Annual OT cap ≤ 300h (or ≤ 200h if not legally eligible; via Settings). Economica


BR-AD-05 Compliance = clamp: when inputs exceed caps, only allowed hours are counted; excess ignored (audit-logged, no warnings).


BR-AD-06 All OT requires employee consent; store digital approval.


BR-AD-07 OT time increments: All OT start/end times must use 15-minute increments (00, 15, 30, 45 minutes). This prevents odd times like 10:07 or 14:23 and ensures consistent time tracking.


BR-AD-08 Weekday OT restrictions: OT on weekdays (Monday-Friday) is only allowed from 19:00-22:00 with a maximum of 2 hours. This ensures employees complete regular work hours (08:00-17:00) before starting OT. Weekend and holiday OT follows general time range rules (06:00-22:00).


C) Overtime Pay (BR-OT — no night rules)
BR-OT-01 OT multipliers: 150% (weekday), 200% (weekly rest day), 300% (public holiday/paid day). Vietnam Briefing+1


BR-OT-02 [CHANGED] If a public holiday falls on a weekly rest day, grant a paid substitute day on the next working day; work on the substitute day is paid at weekly rest-day rate (200%), not 300%. natlex.ilo.org+1


BR-OT-03 Weekend/holiday work obeys caps (company: ≤10h/day, ≤48h/week). These OT hours count toward monthly (40h) and annual (300h/200h) caps. Vietnam Briefing


D) Leave (BR-LV — tuned for no SI integration for now)
BR-LV-01 Paid public holidays (Labor Code): 11 days total — New Year (1), Tet (5), Hung Kings (1), Reunification 30/4 (1), Labor 1/5 (1), National Day (2). Foreign employees: +1 traditional New Year +1 National Day of their country. natlex.ilo.org


BR-LV-02 Compensatory rule: holiday on rest day → paid substitute day next working day. natlex.ilo.org


BR-LV-03 Annual leave after 12 months: 12/14/16 days depending on conditions; pro-rate if <12 months; +1 day per 5 years with the same employer. Tilleke & Gibbins


BR-LV-04 Personal leave (paid): 3 days (employee marriage); 1 day (child marriage); 3 days (death of parent/spouse/child). Certain relatives: 1 day unpaid; more by agreement. (Company may request documents.)


BR-LV-05 [CHANGED] Maternity recorded per company rules (no SI processing in system). Paternity (insured fathers in law): 5–14 working days, to be taken within 60 days from birth (effective update reflected by authorities). System notes the 60-day window for future SI integration; currently treat pay per company policy. vss.gov.vn+1


BR-LV-06 Sick leave (company policy): configurable paid/unpaid and optional company quota; system does not handle SI claim logic yet.


BR-LV-07 Evidence & payroll: personal leave paid requires documents; maternity/sick recorded per company rules; employer stores evidence (future SI claims if enabled).


BR-LV-08 Scheduling: employer sets annual leave schedule after consultation; leave may be split/combined up to 3 years; unused annual leave is paid upon termination.
 Half-day rules (company scope)


BR-LV-09 Half-day leave = 0.5 day; only for a single working day (not weekends/holidays). Periods: AM (08:00–12:00) or PM (13:00–17:00). Store JSON: {"isHalfDay":true,"halfDayPeriod":"AM|PM","durationDays":0.5}. Validate: (1) working day, (2) no full-day leave same date, (3) no same-period half-day exists, (4) sufficient balance for paid types.


BR-LV-10 Overlap validation: block full-day + half-day on the same date; allow AM and PM half-days as separate requests; 0.5 + 0.5 = 1.0 day deduction. Check both PENDING and APPROVED for conflicts.


BR-LV-11 Attendance interaction: AM leave suppresses late flag; PM leave suppresses early flag. Working hours = ShiftHours − HalfDayHours (default 8–4=4). OT after 17:00 still allowed with PM half-day. Only APPROVED half-day affects attendance.


BR-LV-12 [ADAPTED] Half-day applies to Annual, Personal, Unpaid. Not applicable to Maternity/Sick (since SI logic is not implemented).


Paid leave (Annual/Personal): deduct 0.5 day from balance; no salary deduction.


Unpaid: deduct 0.5 × DayRate; no balance deduction.


E) Earnings & Deductions (BR-DE — company payroll)
BR-DE-01 Rates: HourlyRate = BaseMonthlySalary ÷ StandardMonthlyHours; DayRate = HourlyRate × StandardDailyHours (e.g., 8h).


BR-DE-02 Late/early beyond tolerance → Half-day pay = 0.5 × DayRate.


BR-DE-03 Too few hours: if total worked < 3h/day → Zero-day pay (DayPay = 0).


BR-DE-04 No per-minute fines; only two outcomes: Half-day or Zero-day per rules above.


BR-DE-05 Payslip transparency: show “Half-day pay” / “Zero-day pay” with basis; configurable: StandardDailyHours, ToleranceMinutes, MinHoursForAnyPay = 3h.


Notes to testers/devs (legal anchors): public-holiday list & comp day rule (Labor Code) ; OT caps and 150/200/300% multipliers; paid break ≥30’ for ≥6h continuous shift; paternity leave to be taken within 60 days. vss.gov.vn+4natlex.ilo.org+4Vietnam Briefing+4

TIẾNG VIỆT — Bộ BR hoàn chỉnh (không làm đêm, tính theo policy công ty)
A) Chấm công & Quy trình (BR-01 … BR-26)
BR-01 Quản lý chỉ truy cập dữ liệu chấm công của nhân viên trong phòng ban mình quản lý.


BR-02 HR/HRM xem được tất cả phòng ban.


BR-03 Khi kỳ ở trạng thái Locked, mọi bản ghi trong kỳ là chỉ-đọc với quản lý.


BR-04 Team Attendance bắt buộc chọn khoảng ngày hoặc kỳ công; khoảng ngày ≤ 31 ngày.


BR-05 [ĐÃ SỬA] Giờ làm = Giờ ra − Giờ vào − Nghỉ không lương − phần trừ Đi muộn/Về sớm. Nếu ca ngày liên tục ≥ 6 giờ, phải có nghỉ giữa ca tối thiểu 30 phút và thời gian này được tính vào giờ làm (không trừ). Thư Viện Nhà Đất


BR-06 Làm tròn: giờ vào làm tròn xuống, giờ ra làm tròn lên; đơn vị làm tròn cấu hình (RoundingUnitMinutes).


BR-07 Ân hạn/Ngưỡng trước khi gắn cờ Đi muộn/Về sớm: ToleranceMinutes (mặc định 5).


BR-08 Giờ OT = max(0, Giờ làm − Giờ ca); áp hệ số theo BR-OT-01.


BR-09 Ưu tiên trạng thái: Nghỉ phép đã duyệt ghi đè Vắng/Thiếu/Đi muộn/Về sớm.


BR-10 Điều chỉnh chỉ khi kỳ chưa Lock và không chồng lấn nghỉ phép đã duyệt (trừ khi HR cho phép override).


BR-11 Duyệt điều chỉnh tính lại Giờ làm/OT và lưu người duyệt, thời điểm, ghi chú.


BR-12 Từ chối điều chỉnh phải có lý do.


BR-13 Không được Submit kỳ nếu còn điều chỉnh đang chờ duyệt.


BR-14 Submit cho HR → trạng thái Submitted; quản lý chỉ-đọc các bản ghi đó.


BR-15 Chỉ HR được Lock; Unlock cần HRM phê duyệt và lý do (ghi audit).


BR-16 Generate Payroll chỉ với kỳ Locked.


BR-17 Sửa công sau khi generate payroll → gắn cờ Dirty; phải Regenerate trước khi trả lương.


BR-18 Export theo bộ lọc hiện tại, kèm tổng: Giờ làm, OT, Đi muộn, Vắng.


BR-19 Khi có điều chỉnh mới sẽ notify quản lý; quá hạn theo SLA sẽ escalate lên HR.


BR-20 Nút Approve/Submit bị vô hiệu khi chưa đủ điều kiện; hiển thị tooltip giải thích điều kiện còn thiếu.


BR-21 Quản lý không sửa trực tiếp giờ vào/ra; mọi thay đổi qua điều chỉnh đã duyệt.


BR-22 Nghỉ cả ngày → Giờ làm = 0, không OT, không tạo bản ghi Vắng.


BR-23 Nghỉ nửa ngày dùng cấu hình HalfDayHours (mặc định 4 giờ); không gắn cờ Đi muộn/Về sớm trong khung nghỉ. Nghỉ sáng (08:00–12:00) không gắn cờ đi muộn; nghỉ chiều (13:00–17:00) không gắn cờ về sớm. Chỉ cho ngày làm việc, không phải cuối tuần/ngày lễ.


BR-24 Xem/duyệt/submit/lock/unlock đều ghi audit đầy đủ (ai/khi nào/giá trị trước–sau).


BR-25 Submit/Lock kỳ sẽ gửi thông báo tới quản lý & HR liên quan.


BR-26 Chỉ vai trò Dept Manager được duyệt điều chỉnh cho đội mình.


B) Giới hạn bổ sung (BR-AD — chỉ ca ngày; trần công ty)
BR-AD-01 Chỉ làm ban ngày: chặn 22:00–06:00 (không bật tính năng ca đêm).


BR-AD-02 Trần công ty: Tổng (thường + OT) ≤ 10h/ngày; ≤ 48h/tuần.
 Ghi chú: Bộ luật Lao động cho phép tới 10wh/ngày (kể cả OT), ≤ 40h OT/tháng, và thường ≤ 200h/năm (một số ngành ≤ 300h); công ty giữ 10h/ngày để an toàn. Vietnam Briefing+2aseanbriefing.com+2


BR-AD-03 Trần OT tháng ≤ 40h (bao gồm T7-CN/ngày lễ). Vietnam Briefing


BR-AD-04 Trần OT năm ≤ 300h (hoặc ≤ 200h nếu không thuộc diện pháp lý; cấu hình trong Settings). Economica


BR-AD-05 Tuân thủ = cắt ngưỡng: phần vượt chỉ ghi audit, không tính.


BR-AD-06 Mọi OT phải có đồng ý của NLĐ; lưu phê duyệt điện tử.


BR-AD-07 Bước nhảy thời gian OT: Tất cả giờ bắt đầu/kết thúc OT phải theo bước 15 phút (00, 15, 30, 45 phút). Điều này tránh các giờ lẻ như 10:07 hay 14:23 và đảm bảo theo dõi thời gian nhất quán.


BR-AD-08 Giới hạn OT ngày thường: OT trong ngày làm việc (Thứ 2-6) chỉ được phép từ 19:00-22:00 với tối đa 2 giờ. Điều này đảm bảo nhân viên hoàn thành giờ làm thường (08:00-17:00) trước khi bắt đầu OT. OT cuối tuần và ngày lễ theo quy tắc khung giờ chung (06:00-22:00).


C) Tăng ca & trả công (BR-OT — không áp quy tắc ban đêm)
BR-OT-01 Hệ số OT: 150% (ngày thường), 200% (ngày nghỉ hằng tuần), 300% (ngày lễ/nghỉ hưởng lương). Vietnam Briefing+1


BR-OT-02 [ĐÃ SỬA] Lễ trùng ngày nghỉ tuần → nghỉ bù có lương vào ngày làm việc kế tiếp; làm việc trong ngày nghỉ bù trả 200% (như ngày nghỉ tuần), không phải 300%. natlex.ilo.org+1


BR-OT-03 Làm T7-CN/ngày lễ vẫn phải tuân trần (công ty: ≤10h/ngày, ≤48h/tuần). Giờ OT này cộng vào trần tháng (40h) và năm (300h/200h). Vietnam Briefing


D) Nghỉ (BR-LV — tinh chỉnh theo chưa tích hợp BHXH)
BR-LV-01 Nghỉ lễ hưởng lương: 11 ngày — Tết Dương (1), Tết Âm (5), Giỗ Tổ (1), 30/4 (1), 1/5 (1), Quốc khánh (2). NLĐ nước ngoài: +1 Tết truyền thống +1 Quốc khánh nước họ. natlex.ilo.org


BR-LV-02 Quy tắc nghỉ bù: lễ trùng ngày nghỉ tuần → nghỉ bù có lương ngày làm việc kế tiếp. natlex.ilo.org


BR-LV-03 Phép năm sau 12 tháng: 12/14/16 ngày tùy điều kiện; tỷ lệ nếu <12 tháng; +1 ngày mỗi 5 năm làm việc cho cùng NSDLĐ. Tilleke & Gibbins


BR-LV-04 Việc riêng (hưởng lương): 3 ngày (NLĐ kết hôn); 1 ngày (con kết hôn); 3 ngày (bố/mẹ/vợ/chồng/con qua đời). Một số thân nhân: 1 ngày không lương; có thể thỏa thuận thêm (có thể yêu cầu giấy tờ).


BR-LV-05 [ĐÃ SỬA] Thai sản ghi nhận theo policy công ty (hệ thống chưa xử lý hồ sơ BHXH). Cha nghỉ thai sản (đang thuộc diện BHXH theo luật): 5–14 ngày làm việc, phải nghỉ trong vòng 60 ngày từ khi vợ sinh (cập nhật hiệu lực được cơ quan BHXH hướng dẫn). Hệ thống lưu cửa sổ 60 ngày để tương lai tích hợp BHXH; hiện thời tính lương theo policy công ty. vss.gov.vn+1


BR-LV-06 Nghỉ ốm (policy công ty): cấu hình trả/không trả và (nếu muốn) quota công ty; hệ thống chưa tính theo chế độ BHXH.


BR-LV-07 Chứng từ & lương: việc riêng hưởng lương cần giấy tờ; thai sản/ốm ghi nhận theo quy định nội bộ; doanh nghiệp lưu bằng chứng (để dùng khi bật BHXH sau này).


BR-LV-08 Lịch nghỉ: NSDLĐ lập kế hoạch nghỉ năm sau khi tham khảo NLĐ; có thể chia/gộp tối đa 3 năm; thanh toán phép còn lại khi chấm dứt HĐLĐ.
 Quy tắc nửa ngày (phạm vi công ty)


BR-LV-09 Nghỉ nửa ngày = 0.5 ngày; chỉ cho một ngày làm việc (không áp T7-CN/ngày lễ). Hai ca: Sáng (08:00–12:00), Chiều (13:00–17:00). Lưu JSON: {"isHalfDay":true,"halfDayPeriod":"AM|PM","durationDays":0.5}. Kiểm tra: (1) là ngày làm việc, (2) không có nghỉ cả ngày cùng ngày, (3) không có nghỉ nửa ngày trùng ca, (4) đủ số dư với loại có lương.


BR-LV-10 Chồng lấn: chặn nghỉ cả ngày và nửa ngày cùng ngày; cho phép nửa ngày sáng & chiều tách đơn; 0.5 + 0.5 = 1.0 ngày. Kiểm tra cả PENDING & APPROVED.


BR-LV-11 Tương tác với chấm công: nghỉ sáng ẩn cờ đi muộn; nghỉ chiều ẩn cờ về sớm. Giờ làm = Giờ ca − Giờ nửa ngày (mặc định 8−4=4). Vẫn có thể OT sau 17:00 khi nghỉ chiều. Chỉ APPROVED mới ảnh hưởng tính công.


BR-LV-12 [ĐIỀU CHỈNH] Nửa ngày áp dụng cho Annual, Personal, Unpaid. Không áp dụng cho Thai sản/Ốm (vì hệ thống chưa triển khai BHXH).


Nghỉ có lương (Annual/Personal): trừ 0.5 ngày khỏi số dư; không trừ lương.


Unpaid: trừ 0.5 × Lương ngày; không trừ số dư.


E) Thu nhập & Khấu trừ (BR-DE — tính theo policy công ty)
BR-DE-01 Đơn giá: Đơn giá giờ = Lương tháng cơ bản ÷ Giờ chuẩn/tháng; Lương ngày = Đơn giá giờ × Giờ chuẩn/ngày (vd 8h).


BR-DE-02 Đi muộn/Về sớm vượt ngưỡng → nửa ngày lương = 0.5 × Lương ngày.


BR-DE-03 Làm quá ít: nếu tổng giờ làm < 3h/ngày → không lương ngày (DayPay = 0).


BR-DE-04 Không phạt theo phút; chỉ hai mức: Half-day hoặc Zero-day theo quy định.


BR-DE-05 Phiếu lương minh bạch: hiển thị “Half-day pay” / “Zero-day pay” và căn cứ; cấu hình: StandardDailyHours, ToleranceMinutes, MinHoursForAnyPay = 3h.


Ghi chú pháp lý điểm tựa để test: danh mục 11 ngày lễ & quy tắc nghỉ bù; giới hạn giờ làm & OT; hệ số 150/200/300; nghỉ giữa ca ≥30’ cho ca liên tục ≥6h; cha nghỉ thai sản trong 60 ngày.




---

## BR-LV-13: Unpaid Leave Limits (Company Policy)

To prevent abuse and ensure business continuity, Unpaid Leave has the following limits:

### Per Request
- **Maximum:** 5 working days per single request
- **Rationale:** Limit disruption to operations; requests >5 days require higher approval level

### Per Month
- **Maximum:** 13 working days per month
- **Rationale:** Approximately 50% of working days; prevents excessive monthly absence

### Per Year
- **Maximum:** 30 calendar days per year (annual allocation)
- **Rationale:** Reasonable annual cap while allowing flexibility for personal needs

### Advance Notice
- **Minimum:** 14 days advance notice required
- **Rationale:** Allow proper planning and resource allocation

### Salary Deduction
- **Full-day unpaid:** Deduct 1.0 × DayRate from salary
- **Half-day unpaid:** Deduct 0.5 × DayRate from salary

### Validation Rules
1. System validates all limits before approving request
2. Exceeding any limit results in automatic rejection with clear error message
3. Manager can override with written justification (audit logged)
4. Monthly limit resets on 1st of each month
5. Annual limit resets on January 1st

### Implementation
- `default_days = 30`: Annual allocation
- `max_days = 5`: Per request limit
- Monthly limit (13 days): Validated in LeaveRequestService
- Advance notice: 14 days (min_advance_notice)

---
