import pandas as pd
import sys

file_path = r"C:\GitHub\GitHub_Study\python\2학년2학기\식품데이터.xlsx"
output_file = "excel_output.txt"

try:
    # 엑셀 파일 읽기
    df = pd.read_excel(file_path, engine='openpyxl')
    
    with open(output_file, "w", encoding="utf-8") as f:
        f.write("### 엑셀 컬럼 목록 ###\n")
        f.write(str(df.columns.tolist()) + "\n\n")
        
        f.write("### 데이터 미리보기 (상위 5행) ###\n")
        f.write(df.head().to_string() + "\n")
        
    print("Analysis complete. Saved to excel_output.txt")

except Exception as e:
    with open(output_file, "w", encoding="utf-8") as f:
        f.write(f"오류 발생: {e}")
    print(f"Error occurred: {e}")
