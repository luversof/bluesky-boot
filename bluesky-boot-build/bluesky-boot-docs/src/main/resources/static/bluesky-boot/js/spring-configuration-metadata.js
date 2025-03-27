document.addEventListener("DOMContentLoaded", async function () {
    const container = document.getElementById("springConfigurationMetadataContainer");
    const templateSource = document.getElementById("spring-configuration-metadata-template").innerHTML;
    const template = Handlebars.compile(templateSource);
    
    const metaDataInfo = [
//        { file: "data/metadata1.json", name: "Metadata 1" },
//        { file: "data/metadata2.json", name: "Metadata 2" }
        { moduleName: "bluesky-boot", title: "Bluesky Boot Properties" },
        { moduleName: "bluesky-boot-autoconfigure", title: "Bluesky Boot Autoconfigure Properties" }
    ]; // 파일 리스트 및 파일 제목 지정

    for (const { moduleName, title, description } of metaDataInfo) {
        try {
            const response = await fetch('properties/' + moduleName + '/spring-configuration-metadata.json');
            const data = await response.json();
            
            // 데이터가 없거나 properties가 없을 경우 기본값 설정
            const context = {
                title: title, // 상단 제목
                description: description,
                properties: data.properties || [] // 속성 리스트
            };

            // 템플릿을 사용하여 테이블 생성 후 삽입
            container.innerHTML += template(context);
        } catch (error) {
            console.error("Error loading JSON:", moduleName, error);
        }
    }
});