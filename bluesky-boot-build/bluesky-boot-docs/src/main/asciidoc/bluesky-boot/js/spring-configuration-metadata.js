document.addEventListener("DOMContentLoaded", async function () {
	const templateSource = document.getElementById("spring-configuration-metadata-template").innerHTML;
	const template = Handlebars.compile(templateSource);
	
	Handlebars.registerHelper("className", function (str) {
		if (typeof str !== "string") return "";
		const parts = str.split(".");
		return parts[parts.length - 1];
	});
	
	const metaDataInfo = [
//		{ file: "data/metadata1.json", name: "Metadata 1" },
//		{ file: "data/metadata2.json", name: "Metadata 2" }
		{ moduleName: "bluesky-boot" },
		{ moduleName: "bluesky-boot-autoconfigure" }
	]; // 파일 리스트 및 파일 제목 지정

	for (const { moduleName, description } of metaDataInfo) {
		try {
			const response = await fetch('properties/' + moduleName + '/spring-configuration-metadata.json');
			const data = await response.json();
			
			// 데이터가 없거나 properties가 없을 경우 기본값 설정
			const context = {
				description: description,
				properties: data.properties || [] // 속성 리스트
			};

			// 템플릿을 사용하여 테이블 생성 후 삽입
			const container = document.getElementById(moduleName + "-container");
			container.innerHTML += template(context);
		} catch (error) {
			console.error("Error loading JSON:", moduleName, error);
		}
	}
});