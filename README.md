<h1 align="center">
  <img src="https://raw.githubusercontent.com/ddc/JetbrainsTrafficLightButtons/refs/heads/main/assets/ddcSoftwaresThemesIcon.svg" alt="ddcSoftwaresThemesIcon" width="150">
  <br>
  Traffic Light Buttons
</h1>

<p align="center">
    <a href="https://github.com/sponsors/ddc"><img src="https://img.shields.io/static/v1?style=plastic&label=Sponsor&message=%E2%9D%A4&logo=GitHub&color=ff69b4" alt="Sponsor"/></a>
    <br>
    <a href="https://ko-fi.com/ddc"><img src="https://img.shields.io/badge/Ko--fi-ddc-FF5E5B?style=plastic&logo=kofi&logoColor=white&color=brightgreen" alt="Ko-fi"/></a>
    <a href="https://www.paypal.com/ncp/payment/6G9Z78QHUD4RJ"><img src="https://img.shields.io/badge/Donate-PayPal-brightgreen.svg?style=plastic&logo=paypal&logoColor=white" alt="Donate"/></a>
    <br>
    <a href="https://github.com/ddc/JetbrainsTrafficLightButtons/blob/main/LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=plastic&logo=apache&logoColor=white" alt="License: Apache 2.0"/></a>
    <a href="https://github.com/ddc/JetbrainsTrafficLightButtons/releases/latest"><img src="https://img.shields.io/github/v/release/ddc/JetbrainsTrafficLightButtons?style=plastic&logo=github&logoColor=white" alt="Release"/></a>
    <br>
    <a href="https://github.com/ddc/JetbrainsTrafficLightButtons/issues"><img src="https://img.shields.io/github/issues/ddc/JetbrainsTrafficLightButtons?style=plastic&logo=github&logoColor=white" alt="issues"/></a>
    <a href="https://github.com/ddc/JetbrainsTrafficLightButtons/actions/workflows/workflow.yml"><img src="https://img.shields.io/github/actions/workflow/status/ddc/JetbrainsTrafficLightButtons/workflow.yml?style=plastic&logo=github&logoColor=white&label=CI%2FCD%20Pipeline" alt="CI/CD Pipeline"/></a>
    <a href="https://actions-badge.atrox.dev/ddc/JetbrainsTrafficLightButtons/goto?ref=main"><img src="https://img.shields.io/endpoint.svg?url=https%3A//actions-badge.atrox.dev/ddc/JetbrainsTrafficLightButtons/badge?ref=main&label=build&logo=github&style=plastic" alt="Build Status"/></a>
</p>

<p align="center">Replaces Close/Minimize/Maximize window buttons with macOS-style traffic light buttons for all JetBrains IDEs (Linux only)</p>

<p align="center">📦 <b><a href="https://plugins.jetbrains.com/plugin/30756-traffic-light-buttons">Install from JetBrains Marketplace</a></b> 📦 </p>


# Table of Contents

- [Screenshot](#screenshot)
- [Features](#features)
- [Installation](#installation)
- [Settings](#settings)
- [Build](#build)
- [License](#license)
- [Support](#support)


# Screenshot

<p align="left">
  <img src="assets/examples.png" alt="Left placement">
</p>


# Features

- LINUX ONLY (macOS already has native traffic lights; Windows support planned for a future release)
- macOS-style traffic light buttons (red/yellow/green)
- Four button states: active, hover (with action icons), pressed, and inactive (gray)
- Configurable button placement (left or right side of the title bar)
- Compatible with all JetBrains IDEs (2025.3.3+)


# Installation

## From Marketplace

1. In your JetBrains IDE, go to **Settings > Plugins > Marketplace**
2. Search for **Traffic Light Buttons**
3. Click **Install** and restart the IDE


## From Plugin ZIP

1. Download the latest `TrafficLightButtons-*.zip` from [Releases](https://github.com/ddc/JetbrainsTrafficLightButtons/releases)
2. Go to **Settings > Plugins > Install Plugin from Disk...**
3. Select the downloaded `.zip` file and restart the IDE


# Settings

`Settings > Appearance & Behavior > Traffic Light Buttons`

- **Button Placement** — Left or Right (default: Right)
- **Button Order** — IDE Default or macOS Style (only available for Right placement)
  - **IDE Default**: Minimize, Maximize, Close
  - **macOS Style**: Maximize, Minimize, Close


# Build

```bash
./build.sh
```

# License

This project is licensed under the [Apache 2.0 License](LICENSE).


# Support

If you find this project helpful, consider supporting development.

<a href='https://github.com/sponsors/ddc' target='_blank'><img height='24' style='border:0px;height:24px;' src='https://img.shields.io/badge/Sponsor-❤-ea4aaa?style=plastic&logo=github&logoColor=white' border='0' alt='Sponsor on GitHub' /></a>
<a href='https://ko-fi.com/ddc' target='_blank'><img height='30' style='border:0px;height:30px;' src='https://storage.ko-fi.com/cdn/kofi2.png?v=6' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>
<a href='https://www.paypal.com/ncp/payment/6G9Z78QHUD4RJ' target='_blank'><img height='30' style='border:0px;height:30px;' src='https://www.paypalobjects.com/digitalassets/c/website/marketing/apac/C2/logos-buttons/optimize/44_Yellow_PayPal_Pill_Button.png' border='0' alt='Donate via PayPal' /></a>
