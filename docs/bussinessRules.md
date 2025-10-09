A) Attendance & Workflow — Final (BR-01 … BR-26)
English
ID	Description
BR-01	Managers can access attendance data only for employees in their own departments.
BR-02	HR/HRM can view all departments.
BR-03	When a period is Locked, all records in that period are read-only for managers.
BR-04	Team Attendance requires a selected date range or period; the date range must be ≤ 31 days.
BR-05	WorkHours = Check-out − Check-in − UnpaidBreaks − Late/Early deduction.
BR-06	Rounding: check-in rounds down, check-out rounds up; rounding unit is configurable (RoundingUnitMinutes).
BR-07	Grace/Tolerance before marking Late/Early: ToleranceMinutes (default 5).
BR-08	OTHours = max(0, WorkHours − ShiftHours); pay multipliers apply per BR-OT-01 if configured.
BR-09	Status precedence: Approved Leave overrides Absent/Missing/Late/Early.
BR-10	Adjustments allowed only in Unlocked periods and must not overlap approved leave (unless explicit HR override).
BR-11	Approving an adjustment recalculates Work/OT and stores approver, timestamp, note.
BR-12	Rejecting an adjustment requires a reason.
BR-13	A period cannot be submitted to HR if any adjustments are pending.
BR-14	Submitting to HR sets status Submitted; managers become read-only on those records.
BR-15	Only HR may Lock; Unlock requires HRM authorization plus a reason (audit-logged).
BR-16	Generate Payroll only for Locked periods.
BR-17	Post-payroll attendance changes flag payroll as Dirty; must Regenerate before payment.
BR-18	Export honors current filters; includes totals: Work, OT, Late, Absent.
BR-19	Managers are notified on new adjustments; overdue items escalate to HR per configured SLA.
BR-20	Approve/Submit buttons disabled until prerequisites are met; show tooltip explaining missing prerequisites.
BR-21	Managers cannot edit check-in/out directly; all changes via approved adjustments.
BR-22	Full-day approved leave → WorkHours = 0, no OT, no Absent entry.
BR-23	Half-day leave uses HalfDayHours; suppress Late/Early within the leave window.
BR-24	View/approve/submit/lock/unlock operations are fully audit-logged (who/when/before–after values).
BR-25	Submitting/locking a period triggers notifications to relevant managers and HR.
BR-26	Only the Dept Manager role may approve adjustments for their teams.
Tiếng Việt
ID	Description
BR-01	Quản lý chỉ truy cập dữ liệu chấm công của nhân viên trong phòng ban mình quản lý.
BR-02	HR/HRM xem được tất cả phòng ban.
BR-03	Khi kỳ ở trạng thái Locked, mọi bản ghi trong kỳ là chỉ-đọc với quản lý.
BR-04	Team Attendance bắt buộc chọn khoảng ngày hoặc kỳ công; khoảng ngày ≤ 31 ngày.
BR-05	Giờ làm = Giờ ra − Giờ vào − Nghỉ không lương − Phần trừ đi muộn/về sớm.
BR-06	Làm tròn: giờ vào làm tròn xuống, giờ ra làm tròn lên; đơn vị làm tròn cấu hình (RoundingUnitMinutes).
BR-07	Ân hạn/Ngưỡng trước khi gắn cờ Đi muộn/Về sớm: ToleranceMinutes (mặc định 5).
BR-08	Giờ OT = max(0, Giờ làm − Giờ ca); hệ số trả công áp theo BR-OT-01 nếu được cấu hình.
BR-09	Ưu tiên trạng thái: Nghỉ phép đã duyệt ghi đè Vắng/Thiếu/Đi muộn/Về sớm.
BR-10	Điều chỉnh chỉ khi kỳ chưa Lock và không chồng lấn nghỉ phép đã duyệt (trừ khi HR cho phép override).
BR-11	Duyệt điều chỉnh tính lại Giờ làm/OT và lưu người duyệt, thời điểm, ghi chú.
BR-12	Từ chối điều chỉnh phải có lý do.
BR-13	Không được Submit kỳ nếu còn điều chỉnh đang chờ duyệt.
BR-14	Submit cho HR → trạng thái Submitted; quản lý chỉ-đọc các bản ghi đó.
BR-15	Chỉ HR được Lock; Unlock cần HRM phê duyệt và lý do (ghi audit).
BR-16	Generate Payroll chỉ với kỳ Locked.
BR-17	Sửa công sau khi generate payroll → cờ Dirty; phải Regenerate trước khi trả lương.
BR-18	Export theo bộ lọc hiện tại, kèm tổng: Giờ làm, OT, Đi muộn, Vắng.
BR-19	Khi có điều chỉnh mới sẽ notify quản lý; quá hạn theo SLA sẽ escalate lên HR.
BR-20	Nút Approve/Submit bị vô hiệu khi chưa đủ điều kiện; hiển thị tooltip giải thích điều kiện còn thiếu.
BR-21	Quản lý không sửa trực tiếp giờ vào/ra; mọi thay đổi qua điều chỉnh đã duyệt.
BR-22	Nghỉ phép cả ngày → Giờ làm = 0, không OT, không tạo bản ghi Vắng.
BR-23	Nghỉ nửa ngày dùng HalfDayHours; không gắn cờ Đi muộn/Về sớm trong khung nghỉ.
BR-24	Xem/duyệt/submit/lock/unlock đều ghi audit đầy đủ (ai/khi nào/giá trị trước–sau).
BR-25	Submit/Lock kỳ sẽ gửi thông báo tới quản lý & HR liên quan.
BR-26	Chỉ vai trò Dept Manager được duyệt điều chỉnh cho đội mình.
Ghi chú: BR-10 đã gộp ý “không chồng lấn nghỉ phép” và cho phép HR override khi thật sự cần (có audit).
________________________________________
B) Additional Limits (BR-AD — giữ nguyên ID)
English
ID	Description
BR-AD-01	Day-shift only: block 22:00–06:00 (no night work).
BR-AD-02	Daily total (regular + OT) ≤ 10h; weekly total ≤ 48h.
BR-AD-03	Monthly OT cap ≤ 40h (includes weekends/holidays).
BR-AD-04	Annual OT cap ≤ 300h (or ≤ 200h if not legally eligible; via Settings).
BR-AD-05	Compliance = clamp: when inputs exceed caps, only allowed hours are counted; excess ignored (audit-logged, no warnings).
BR-AD-06	All OT requires employee consent; store digital approval.
Tiếng Việt
ID	Description
BR-AD-01	Chỉ làm ban ngày: chặn 22:00–06:00 (không ca đêm).
BR-AD-02	Tổng giờ/ngày (thường + OT) ≤ 10h; tổng tuần ≤ 48h.
BR-AD-03	Trần OT tháng ≤ 40h (gồm T7-CN/ngày lễ).
BR-AD-04	Trần OT năm ≤ 300h (hoặc ≤ 200h nếu không đủ điều kiện pháp lý; cấu hình trong Settings).
BR-AD-05	Tuân thủ = cắt ngưỡng: vượt trần thì chỉ tính phần hợp lệ; phần vượt bỏ qua (ghi audit, không cảnh báo).
BR-AD-06	Mọi OT phải có đồng ý của NLĐ; lưu phê duyệt điện tử.
________________________________________
C) Overtime Pay (BR-OT — giữ nguyên ID)
English
ID	Description
BR-OT-01	OT multipliers: 150% (weekday), 200% (weekly rest day), 300% (public holiday/paid leave).
BR-OT-02	Holiday overlaps rest day → grant paid substitute day next working day; work on the substitute day is paid as weekly rest day (200%).
BR-OT-03	Weekend/holiday work obeys caps (≤10h/day, ≤48h/week); excess not counted. These OT hours count toward monthly (40h) and annual (300h/200h) caps.
Tiếng Việt
ID	Description
BR-OT-01	Hệ số OT: 150% (ngày thường), 200% (ngày nghỉ hằng tuần), 300% (ngày lễ/nghỉ hưởng lương).
BR-OT-02	Lễ trùng ngày nghỉ hằng tuần → nghỉ bù có lương ngày làm việc kế tiếp; làm ngày nghỉ bù trả như ngày nghỉ hằng tuần (200%).
BR-OT-03	Làm T7-CN/ngày lễ phải tuân trần (≤10h/ngày, ≤48h/tuần); phần vượt không tính. Giờ OT này cộng vào trần tháng (40h) và năm (300h/200h).
________________________________________
D) Leave (BR-LV — giữ nguyên ID)
English
ID	Description
BR-LV-01	Paid public holidays: New Year’s (1), Tet (5), Hung Kings (1), 30/4 (1), 1/5 (1), National Day (2). Foreign employees: +1 traditional New Year +1 National Day of their country.
BR-LV-02	Compensatory rule: holiday on rest day → paid substitute day next working day.
BR-LV-03	Annual leave after 12 months: 12/14/16 days (per condition). Pro-rate if <12 months; +1 day per 5 years of service.
BR-LV-04	Personal leave (paid): 3 days (employee marriage); 1 day (child marriage); 3 days (death of parent/spouse/child). Some relatives: 1 day unpaid; more by agreement.
BR-LV-05	Maternity & paternity: maternity 6 months (Social Insurance). Paternity (insured fathers) 5–14 working days within 30 days post-birth depending on case.
BR-LV-06	Sick leave (SI): 30/40/60 days by SI seniority; hazardous 40/50/70; long-term up to 180 days. Child-care sickness: 20 days (<3y), 15 days (3–<7y).
BR-LV-07	Evidence & payroll: personal leave paid requires documents; SI-covered leave is paid per SI rules; employer records and coordinates claims.
BR-LV-08	Scheduling: employer sets annual leave schedule after consultation; leave may be split/combined up to 3 years; unused leave paid upon termination.
Tiếng Việt
ID	Description
BR-LV-01	Nghỉ lễ hưởng lương: Tết Dương lịch (1), Tết Âm (5), Giỗ Tổ (1), 30/4 (1), 1/5 (1), Quốc khánh (2). NLĐ nước ngoài: +1 Tết truyền thống +1 Quốc khánh nước họ.
BR-LV-02	Nghỉ bù: lễ trùng ngày nghỉ hằng tuần → nghỉ bù có lương vào ngày làm việc kế tiếp.
BR-LV-03	Phép năm sau 12 tháng: 12/14/16 ngày (tùy điều kiện). Tỷ lệ nếu <12 tháng; +1 ngày mỗi 5 năm thâm niên.
BR-LV-04	Việc riêng (hưởng lương): 3 ngày (NLĐ kết hôn); 1 ngày (con kết hôn); 3 ngày (bố/mẹ/vợ/chồng/con qua đời). Một số thân nhân: 1 ngày không lương; có thể thỏa thuận thêm.
BR-LV-05	Thai sản & cha nghỉ: Thai sản 6 tháng (BHXH). Cha có BHXH: 5–14 ngày làm việc trong 30 ngày sau sinh tùy trường hợp.
BR-LV-06	Ốm đau (BHXH): 30/40/60 ngày theo thâm niên BHXH; nặng nhọc 40/50/70; dài ngày tối đa 180 ngày. Chăm con ốm: 20 ngày (<3t), 15 ngày (3–<7t).
BR-LV-07	Chứng từ & trả lương: việc riêng hưởng lương cần giấy tờ; nghỉ thuộc BHXH trả theo BHXH; DN ghi nhận và phối hợp quyết toán.
BR-LV-08	Lịch nghỉ: NSDLĐ lập kế hoạch nghỉ năm sau khi tham khảo NLĐ; có thể chia/gộp tối đa 3 năm; thanh toán phép còn lại khi chấm dứt HĐLĐ.
________________________________________
E) Earnings & Deductions (BR-DE — giữ nguyên ID)
English
ID	Description
BR-DE-01	Rates: HourlyRate = BaseMonthlySalary ÷ StandardMonthlyHours; DayRate = HourlyRate × StandardDailyHours (e.g., 8h).
BR-DE-02	Late/early beyond tolerance → Half-day pay = 0.5 × DayRate.
BR-DE-03	Too few hours: if total worked < 3h/day → Zero-day pay (DayPay = 0).
BR-DE-04	No per-minute fines; only two outcomes: Half-day or Zero-day per rules above.
BR-DE-05	Payslip transparency: show “Half-day pay” / “Zero-day pay” with basis; configurable: StandardDailyHours, ToleranceMinutes, MinHoursForAnyPay = 3h.
Tiếng Việt
ID	Description
BR-DE-01	Đơn giá: Đơn giá giờ = Lương tháng cơ bản ÷ Giờ làm chuẩn/tháng; Đơn giá ngày = Đơn giá giờ × Giờ chuẩn/ngày (vd 8h).
BR-DE-02	Đi muộn/Về sớm vượt ngưỡng → nửa ngày lương = 0,5 × Đơn giá ngày.
BR-DE-03	Làm quá ít: nếu tổng giờ làm < 3h/ngày → không lương ngày (DayPay = 0).
BR-DE-04	Không phạt theo phút; chỉ áp hai mức: nửa ngày hoặc không lương ngày theo quy định.
BR-DE-05	Minh bạch phiếu lương: hiển thị “Half-day pay” / “Zero-day pay” và căn cứ; cấu hình: StandardDailyHours, ToleranceMinutes, MinHoursForAnyPay = 3h.
________________________________________
F) Giải thích các rule dễ gây nhầm lẫn (tóm tắt để test/triển khai)
●	BR-06 (Rounding): Check-in round down, Check-out round up theo RoundingUnitMinutes. Không áp cho nghỉ/phép.

●	BR-07 (Tolerance): Miễn cờ Late/Early nếu lệch ≤ ToleranceMinutes; không tặng giờ.

●	BR-08 + BR-OT + BR-AD (Tính OT): Tính WorkHours → OTHours = max(0, Work−Shift) → áp multiplier (BR-OT-01) nhưng bị clamp bởi BR-AD-02..05 (ghi audit phần bị cắt).

●	BR-09 (Status precedence): Leave (full/half) ghi đè các cờ vi phạm trong khung leave.

●	BR-10 (Adjustments & overlap leave): Không chồng lấn leave trừ khi có HR override (có audit).

●	BR-15 (Lock/Unlock): Lock chỉ HR; Unlock cần HRM + lý do; luôn before/after trong audit.

●	BR-17 (Dirty payroll): Bất kỳ sửa công sau khi generate → Dirty; phải Regenerate trước khi trả lương.

●	BR-19 (SLA escalation): Điều chỉnh quá hạn theo SLA → escalate HR (thêm notify).

●	BR-20 (Disable & tooltip): Nút chỉ bật khi đủ điều kiện; tooltip nêu thiếu gì (ví dụ còn X pending).

●	BR-23 (Half-day & Late/Early): Trong HalfDayHours đã duyệt thì không gắn cờ Late/Early; ngoài khung vẫn áp BR-06/07.

●	BR-AD-05 (Clamp): Không cảnh báo; cắt phần vượt trần và ghi audit (input, hợp lệ, phần cắt, lý do).

●	BR-OT-02 (Substitute day): Làm ngày nghỉ bù trả 200% (như weekly rest), không 300%.

●	BR-DE-02 vs BR-DE-03: Nếu cùng thỏa, ưu tiên Zero-day (BR-DE-03) hơn Half-day.

