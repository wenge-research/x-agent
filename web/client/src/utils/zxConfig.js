// ttsConfig.js
export function getRandomConfig() {
  const configs = [
    {
      'APPID': 'xxxxxxx',
      'API_SECRET': 'xxxxxxx',
      'API_KEY': 'ddc4e7ce8e45298cd90d5a6a956f2dc8'
    },
    {
      'APPID': '7249d9f8',
      'API_SECRET': 'NDNkOTE4OTU4MzRiOTU2NTM4YTcxZmZj',
      'API_KEY': 'bde01718c8c25772ec51bc91f327163d'
    },
    {
      'APPID': '87b03c6a',
      'API_SECRET': 'YmU4YWE3Yjk0OTg5MzFkZWU4YWRhZTcx',
      'API_KEY': 'e78b4fe8f889387f64ae117e23fe8deb'
    }
  ];

  const randomIndex = Math.floor(Math.random() * configs.length);
  return configs[randomIndex];
}