OUTPUT_FILE = _web.h

all:
	rm -f $(OUTPUT_FILE)
	xxd -i index.html >> $(OUTPUT_FILE)
	xxd -i 404.html >> $(OUTPUT_FILE)
	xxd -i Logo_terasic.jpg >> $(OUTPUT_FILE)
	xxd -i favicon.ico >> $(OUTPUT_FILE)

clear:
	rm -f $(OUTPUT_FILE)
