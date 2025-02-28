import { defineStore } from "pinia";

export const useUrlStore = defineStore("urlStore", {
  state: () => ({
    apiUrl: "", // ⚠️ 后端URL（请替换为你的API地址）
  }),

  actions: {
    async generateShortUrl(longUrl: string, alias?: string) {
      try {
        const response = await fetch(this.apiUrl, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            url: longUrl,
            alias: alias || "", // 如果没有提供自定义后缀，则为空
          }),
        });

        if (!response.ok) throw new Error("Failed to shorten URL");

        const data = await response.json();
        return data.shortUrl;
      } catch (error) {
        console.error(error);
        return null;
      }
    },
  },
});
